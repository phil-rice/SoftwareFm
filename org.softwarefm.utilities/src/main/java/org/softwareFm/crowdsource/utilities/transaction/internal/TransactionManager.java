package org.softwareFm.crowdsource.utilities.transaction.internal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.exceptions.AggregateException;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction1WithExceptionHandler;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.FutureAndMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManagerBuilder;
import org.softwareFm.crowdsource.utilities.transaction.ITransactional;

public class TransactionManager implements ITransactionManagerBuilder {

	public static class DefaultFutureToTransactionDn implements IFunction1<Future<?>, ITransaction<?>> {

		@Override
		public ITransaction<?> apply(final Future<?> future) throws Exception {
			if (future == null)
				throw new NullPointerException();
			return new ITransaction<Object>() {
				@Override
				public Object get() {
					try {
						return future.get();
					} catch (InterruptedException e) {
						throw WrappedException.wrap(e);
					} catch (ExecutionException e) {
						throw WrappedException.wrap(e.getCause());
					}
				}

				@Override
				public Object get(long millisecondsToWait) {
					try {
						return future.get(millisecondsToWait, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						throw WrappedException.wrap(e);
					} catch (ExecutionException e) {
						throw WrappedException.wrap(e.getCause());
					} catch (TimeoutException e) {
						throw WrappedException.wrap(e);
					}
				}

				@Override
				public void cancel() {
					future.cancel(true);
				}

				@Override
				public boolean isCancelled() {
					return future.isCancelled();
				}

				@Override
				public boolean isDone() {
					return future.isDone();
				}
			};
		}

	}

	@SuppressWarnings("rawtypes")
	private final Map<Class<?>, IFunction3> callbackExecutors = Maps.newMap(LinkedHashMap.class);

	private final IServiceExecutor serviceExecutor;

	private final IFunction1<Future<?>, ITransaction<?>> futureToTransactionFn;
	private final Map<IMonitor, ITransaction<?>> monitorToTransaction = Maps.newSynchronisedMap();
	private final Map<ITransaction<?>, List<ITransactional>> transactionalMap = Maps.newSynchronisedMap();
	private final ThreadLocal<IMonitor> monitors = new ThreadLocal<IMonitor>();
	private final AtomicInteger activeJobs = new AtomicInteger();

	public TransactionManager(IServiceExecutor serviceExecutor, IFunction1<Future<?>, ITransaction<?>> futureToTransactionFn) {
		this.serviceExecutor = serviceExecutor;
		this.futureToTransactionFn = futureToTransactionFn;
	}

	@Override
	public <Intermediate, Result> ITransaction<Result> start(final IFunction1<IMonitor, Intermediate> job, final IFunction1<Intermediate, Result> resultCallback, Object... potentialTransactionals) {
		IMonitor existing = monitors.get();
		if (existing == null)
			return newTransaction(job, resultCallback, potentialTransactionals);
		else
			return nestedTransaction(existing, job, resultCallback, potentialTransactionals);
	}
	
	@Override
	public boolean inTransaction() {
		return monitors.get() != null;
	}

	private <Result, Intermediate> ITransaction<Result> nestedTransaction(IMonitor monitor, final IFunction1<IMonitor, Intermediate> job, final IFunction1<Intermediate, Result> resultCallback, Object... potentialTransactionals) {
		try {
			ITransaction<?> existing = monitorToTransaction.get(monitor);
			addPotentialTransactionals(existing, potentialTransactionals);
			Intermediate intermediate = job.apply(monitor);
			Result result = executeCallbackFunction(resultCallback, intermediate);
			return ITransaction.Utils.doneTransaction(result);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	private <Result, Intermediate> ITransaction<Result> newTransaction(final IFunction1<IMonitor, Intermediate> job, final IFunction1<Intermediate, Result> resultCallback, Object... potentialTransactionals) {
		activeJobs.incrementAndGet();
		final CountDownLatch latch = new CountDownLatch(1);
		IFunction1<IMonitor, Result> fullJob = new IFunction1<IMonitor, Result>() {
			@Override
			public Result apply(IMonitor monitor) throws Exception {
				AtomicReference<Exception> exception = new AtomicReference<Exception>();
				try {
					monitor.beginTask(toString(), 2);
					assert monitors.get() == null;
					monitors.set(monitor);
					latch.await();
					Intermediate intermediate = job.apply(monitor);
					monitor.worked(1);
					Result result = executeCallbackFunction(resultCallback, intermediate);
					return result;
				} catch (Exception e) {
					exception.set(e);
					throw e;
				} finally {
					try {
						ITransaction<?> transaction = monitorToTransaction.remove(monitor);
						if (transaction == null)
							throw new IllegalStateException("The transaction was null");
						if (exception.get() == null) {
							commit(transaction);
						} else {
							rollback(resultCallback, exception, transaction);
						}
					} finally {
						activeJobs.decrementAndGet();
					}

				}
			}

			private void rollback(final IFunction1<Intermediate, Result> resultCallbackFn, AtomicReference<Exception> exception, ITransaction<?> transaction) throws Exception {
				CopyOnWriteArrayList<Exception> exceptions = new CopyOnWriteArrayList<Exception>();
				try {
					if (resultCallbackFn instanceof IFunction1WithExceptionHandler<?, ?>)
						((IFunction1WithExceptionHandler<?, ?>) resultCallbackFn).handle(exception.get());
				} catch (Exception e) {
					exceptions.add(e);
				}
				for (ITransactional transactional : Maps.getOrEmptyList(transactionalMap, transaction))
					try {
						transactional.rollback();
					} catch (Exception e) {
						exceptions.add(e);
					}
				if (exceptions.size() > 0)
					throw new AggregateException(Lists.addAtStart(exceptions, exception.get()));
			}

			private void commit(ITransaction<?> transaction) throws Exception {
				CopyOnWriteArrayList<Exception> exceptions = new CopyOnWriteArrayList<Exception>();
				List<ITransactional> transactionals = Maps.getOrEmptyList(transactionalMap, transaction);
				for (ITransactional transactional : transactionals)
					try {
						transactional.commit();
					} catch (Exception e) {
						exceptions.add(e);
					}
				switch (exceptions.size()) {
				case 0:
					break;
				case 1:
					throw Lists.getOnly(exceptions);
				default:
					throw new AggregateException(exceptions);
				}
			}

			@Override
			public String toString() {
				return job.toString();
			}
		};
		try {
			FutureAndMonitor<Result> futureAndMonitor = serviceExecutor.submit(fullJob);
			@SuppressWarnings("unchecked")
			ITransaction<Result> transaction = (ITransaction<Result>) futureToTransactionFn.apply(futureAndMonitor.future);
			addPotentialTransactionals(transaction, potentialTransactionals);
			monitorToTransaction.put(futureAndMonitor.monitor, transaction);
			latch.countDown();
			return transaction;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <Intermediate, Result> Result executeCallbackFunction(final IFunction1<Intermediate, Result> resultCallback, Intermediate intermediate) throws Exception {
		for (Entry<Class<?>, IFunction3> entry : callbackExecutors.entrySet())
			if (entry.getKey().isAssignableFrom(resultCallback.getClass())) {
				return (Result) entry.getValue().apply(serviceExecutor, resultCallback, intermediate);
			}
		return resultCallback.apply(intermediate);
	}

	private <Result> void addPotentialTransactionals(ITransaction<Result> transaction, Object... potentialTransactionals) {
		for (Object potential : potentialTransactionals)
			if (potential instanceof ITransactional)
				Maps.addToList(transactionalMap, transaction, (ITransactional) potential);
	}

	@Override
	public <T> void addResource(ITransaction<T> transaction, ITransactional transactional) {
	}

	@Override
	public <Intermediate, Result> ITransactionManagerBuilder registerCallbackExecutor(Class<?> markerClass, IFunction3<IServiceExecutor, IFunction1<Intermediate, Result>, Intermediate, Result> executor) {
		callbackExecutors.put(markerClass, executor);
		return this;
	}

	@Override
	public void shutdown() {
		serviceExecutor.shutdown();
	}

	@Override
	public void shutdownAndAwaitTermination(long timeout, TimeUnit unit) {
		serviceExecutor.shutdownAndAwaitTermination(timeout, unit);
	}

	@Override
	public int activeJobs() {
		return activeJobs.get();
	}

}

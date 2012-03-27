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
import java.util.concurrent.atomic.AtomicReference;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback3;
import org.softwareFm.crowdsource.utilities.callbacks.ICallbackWithExceptionHandler;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.exceptions.AggregateException;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManagerBuilder;
import org.softwareFm.crowdsource.utilities.transaction.ITransactional;

public class TransactionManager implements ITransactionManagerBuilder {
	private final Map<Class<?>, ICallback3<IServiceExecutor, ICallback<?>, Object>> callbackExecutors = new LinkedHashMap<Class<?>, ICallback3<IServiceExecutor, ICallback<?>, Object>>();

	private final IServiceExecutor serviceExecutor;

	private final Map<IFunction1<IMonitor, ?>, ITransaction<?>> fullJobToTransaction = Maps.newSynchronisedMap();
	private final Map<ITransaction<?>, List<ITransactional>> transactionalMap = Maps.newSynchronisedMap();

	public TransactionManager(IServiceExecutor serviceExecutor) {
		this.serviceExecutor = serviceExecutor;
	}

	@Override
	public <T> ITransaction<T> start(final IFunction1<IMonitor, T> job, final ICallback<T> resultCallback, Object... potentialTransactionals) {
		final CountDownLatch latch = new CountDownLatch(1);
		IFunction1<IMonitor, T> fullJob = new IFunction1<IMonitor, T>() {
			@Override
			public T apply(IMonitor monitor) throws Exception {
				AtomicReference<Exception> exception = new AtomicReference<Exception>();
				try {
					latch.await();
					T result = job.apply(monitor);
					executeCallback(resultCallback, result);
					return result;
				} catch (Exception e) {
					exception.set(e);
					throw e;
				} finally {
					ITransaction<?> transaction = fullJobToTransaction.remove(this);
					if (transaction == null)
						throw new IllegalStateException("The transaction was null");
					if (exception.get() == null) {
						commit(transaction);
					} else {
						rollback(resultCallback, exception, transaction);
					}

				}
			}

			private void rollback(final ICallback<T> resultCallback, AtomicReference<Exception> exception, ITransaction<?> transaction) throws Exception {
				CopyOnWriteArrayList<Exception> exceptions = new CopyOnWriteArrayList<Exception>();
				try {
					if (resultCallback instanceof ICallbackWithExceptionHandler<?>)
						((ICallbackWithExceptionHandler<?>) resultCallback).handle(exception.get());
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
				for (ITransactional transactional : Maps.getOrEmptyList(transactionalMap, transaction))
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

			private void executeCallback(final ICallback<T> resultCallback, T result) throws Exception {
				for (Entry<Class<?>, ICallback3<IServiceExecutor, ICallback<?>, Object>> entry : callbackExecutors.entrySet())
					if (entry.getKey().isAssignableFrom(resultCallback.getClass())) {
						entry.getValue().process(serviceExecutor, resultCallback, result);
						return;
					}
				resultCallback.process(result);
			}
		};
		Future<T> future = serviceExecutor.submit(fullJob);
		ITransaction<T> transaction = getTransaction(future);
		for (Object potential : potentialTransactionals)
			if (potential instanceof ITransactional)
				Maps.addToList(transactionalMap, transaction, (ITransactional) potential);
		fullJobToTransaction.put(fullJob, transaction);
		latch.countDown();
		return transaction;
	}

	<T> ITransaction<T> getTransaction(final Future<T> future) {
		if (future == null)
			throw new NullPointerException();
		return new ITransaction<T>() {
			@Override
			public T get() {
				try {
					return future.get();
				} catch (InterruptedException e) {
					throw WrappedException.wrap(e);
				} catch (ExecutionException e) {
					throw WrappedException.wrap(e.getCause());
				}
			}

			@Override
			public T get(long millisecondsToWait) {
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

	@Override
	public <T> void addResource(ITransaction<T> transaction, ITransactional transactional) {
	}

	@Override
	public <C extends ICallback<?>> ITransactionManagerBuilder registerCallbackExecutor(Class<C> markerClass, ICallback3<IServiceExecutor, ICallback<?>, Object> executor) {
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

}

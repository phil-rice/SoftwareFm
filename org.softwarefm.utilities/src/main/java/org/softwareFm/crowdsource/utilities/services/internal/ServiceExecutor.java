package org.softwareFm.crowdsource.utilities.services.internal;

import java.text.MessageFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.softwareFm.crowdsource.utilities.constants.CommonMessages;
import org.softwareFm.crowdsource.utilities.constants.UtilityMessages;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.future.Futures;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.FutureAndMonitor;
import org.softwareFm.crowdsource.utilities.services.IExceptionListener;
import org.softwareFm.crowdsource.utilities.services.IMonitorFactory;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutorLifeCycleListener;
import org.softwareFm.crowdsource.utilities.services.ServerExecutorException;

public class ServiceExecutor implements IServiceExecutor {
	private final ThreadPoolExecutor service;
	private final CopyOnWriteArrayList<IExceptionListener> exceptionListeners = new CopyOnWriteArrayList<IExceptionListener>();
	private final CopyOnWriteArrayList<IServiceExecutorLifeCycleListener> lifeCycleListeners = new CopyOnWriteArrayList<IServiceExecutorLifeCycleListener>();
	private final IMonitorFactory monitorFactory;
	private final CopyOnWriteArrayList<IMonitor> monitors = new CopyOnWriteArrayList<IMonitor>();

	public ServiceExecutor(final String pattern, IMonitorFactory monitorFactory, int threadPoolSize) {
		this.monitorFactory = monitorFactory;

		service = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000), new NamedThreadFactory(pattern)) {

			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);
			}

		};
	}

	@Override
	public void addLifeCycleListener(IServiceExecutorLifeCycleListener listener1) {
		lifeCycleListeners.add(listener1);
	}

	@Override
	public <T> FutureAndMonitor<T> submit(final IFunction1<IMonitor, T> task) {
		try {
			final IMonitor monitor = monitorFactory.createMonitor();
			final CountDownLatch waitUntilSetupComplete = new CountDownLatch(1);
			final CountDownLatch waitUntilMonitorSet = new CountDownLatch(1);
			final AtomicReference<IMonitor> actualMonitor = new AtomicReference<IMonitor>();
			Future<T> rawFuture = service.submit(new Callable<T>() {
				@Override
				public T call() throws Exception {
					Thread currentThread = Thread.currentThread();
					String startName = currentThread.getName();
					currentThread.setName(startName + ": " + task);

					TrackBeginMonitor trackingMonitor = new TrackBeginMonitor(monitor, task);
					actualMonitor.set(trackingMonitor);
					waitUntilMonitorSet.countDown();
					waitUntilSetupComplete.await();
					try {
						for (IServiceExecutorLifeCycleListener listener : lifeCycleListeners)
							listener.starting(task);
						T result = task.apply(trackingMonitor);
						for (IServiceExecutorLifeCycleListener listener : lifeCycleListeners)
							listener.finished(task, result);
						trackingMonitor.checkHasBegan();
						return result;
					} catch (Exception e) {
						if (e != null) {
							for (IExceptionListener listener : exceptionListeners)
								listener.exceptionOccured(e);
							for (IServiceExecutorLifeCycleListener listener : lifeCycleListeners)
								listener.exception(task, e);
						}
						throw e;
					} finally {
						monitors.remove(monitor);
						currentThread.setName(startName);
					}
				}
			});
			waitUntilMonitorSet.await();
			monitors.add(monitor);
			waitUntilSetupComplete.countDown();
			return new FutureAndMonitor<T>(Futures.bindToMonitor(rawFuture, monitor), actualMonitor.get());
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		}
	}

	public static class TrackBeginMonitor implements IMonitor {
		private final IMonitor delegate;
		private boolean hasBegan;
		private final IFunction1<IMonitor, ?> task;

		public TrackBeginMonitor(IMonitor delegate, IFunction1<IMonitor, ?> task) {
			this.delegate = delegate;
			this.task = task;
		}

		@Override
		public void beginTask(String name, int totalWork) {
			delegate.beginTask(name, totalWork);
			hasBegan = true;
		}

		@Override
		public void setTaskName(String name) {
			delegate.setTaskName(name);
		}

		@Override
		public void worked(int work) {
			delegate.worked(work);
		}

		@Override
		public void cancel() {
			delegate.cancel();
		}

		@Override
		public boolean isCanceled() {
			return delegate.isCanceled();
		}

		@Override
		public void done() {
			delegate.done();
		}

		public void checkHasBegan() {
			boolean needsBeginCalling = !delegate.isCanceled();
			if (needsBeginCalling && !hasBegan)
				throw new IllegalStateException(MessageFormat.format(CommonMessages.monitorWasNotBegun, task));
		}

		public IMonitor getDelegate() {
			return delegate;
		}
	}

	@Override
	public void addExceptionListener(IExceptionListener listener) {
		exceptionListeners.add(listener);

	}

	@Override
	public void shutdown() {
		service.shutdown();
	}

	@Override
	public void shutdownAndAwaitTermination(long time, TimeUnit unit) {
		try {
			service.shutdown();
			for (IMonitor monitor : monitors)
				monitor.cancel();
			boolean suceeded = service.awaitTermination(time, unit);
			if (!suceeded) {
				throw new ServerExecutorException(UtilityMessages.cannotCloseServer);
			}
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		}
	}
}
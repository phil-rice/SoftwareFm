package org.softwareFm.common.services.internal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.softwareFm.common.constants.UtilityMessages;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.future.Futures;
import org.softwareFm.common.monitor.IMonitor;
import org.softwareFm.common.services.IExceptionListener;
import org.softwareFm.common.services.IMonitorFactory;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.services.IServiceExecutorLifeCycleListener;
import org.softwareFm.common.services.ServerExecutorException;

public class ServiceExecutor implements IServiceExecutor {
	private final ExecutorService service;
	private final CopyOnWriteArrayList<IExceptionListener> exceptionListeners = new CopyOnWriteArrayList<IExceptionListener>();
	private final CopyOnWriteArrayList<IServiceExecutorLifeCycleListener> lifeCycleListeners = new CopyOnWriteArrayList<IServiceExecutorLifeCycleListener>();
	private final IMonitorFactory monitorFactory;

	public ServiceExecutor(IMonitorFactory monitorFactory, int threadPoolSize) {
		this.monitorFactory = monitorFactory;
		service = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000)) {
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
	public <T> Future<T> submit(final IFunction1<IMonitor, T> task) {
		final IMonitor rawMonitor = monitorFactory.createMonitor();
		final CountDownLatch latch = new CountDownLatch(1);
		Future<T> rawFuture = service.submit(new Callable<T>() {
			@Override
			public T call() throws Exception {
				latch.await();
				try {
					for (IServiceExecutorLifeCycleListener listener : lifeCycleListeners)
						listener.starting(task);
					T result = task.apply(rawMonitor);
					for (IServiceExecutorLifeCycleListener listener : lifeCycleListeners)
						listener.finished(task, result);
					return result;
				} catch (Exception e) {
					if (e != null) {
						for (IExceptionListener listener : exceptionListeners)
							listener.exceptionOccured(e);
						for (IServiceExecutorLifeCycleListener listener : lifeCycleListeners)
							listener.exception(task, e);
					}
					throw e;
				}
			}
		});
		latch.countDown();
		return Futures.bindToMonitor(rawFuture,rawMonitor);
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
			boolean suceeded = service.awaitTermination(time, unit);
			if (!suceeded) {
				throw new ServerExecutorException(UtilityMessages.cannotCloseServer);
			}
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		}
	}
}
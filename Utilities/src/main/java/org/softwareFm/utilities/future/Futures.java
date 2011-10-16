package org.softwareFm.utilities.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.utilities.callbacks.ICallback;

public class Futures {

	public static <T> Future<T> doneFuture(final T value) {
		return new Future<T>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				return value;
			}

			@Override
			public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				return value;
			}
		};
	}

	public static <T> GatedMockFuture<T> gatedMock(ICallback<T> callback, final T value) {
		return new GatedMockFuture<T>(callback, value);
	}

}

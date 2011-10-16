package org.softwareFm.utilities.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;

public class GatedMockFuture<T> implements Future<T> {

	private final T value;
	private boolean done;
	private final ICallback<T> callback;

	public GatedMockFuture(ICallback<T> callback, T value) {
		super();
		this.callback = callback;
		this.value = value;
	}

	public void kick() {
		try {
			callback.process(value);
			done = true;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public boolean cancel(boolean arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		if (!done)
			throw new IllegalStateException();
		return value;
	}

	@Override
	public T get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException {
		if (!done)
			throw new IllegalStateException();
		return value;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return done;
	}

}

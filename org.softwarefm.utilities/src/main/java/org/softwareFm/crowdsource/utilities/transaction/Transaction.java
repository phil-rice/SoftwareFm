package org.softwareFm.crowdsource.utilities.transaction;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;

public class Transaction<T> implements ITransaction<T> {

	private final long defaultTimeout;
	private boolean cancelled;
	private final Future<T> future;

	public Transaction(Future<T> future, long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
		this.future = future;
	}

	@Override
	public T get(long millisecondsToWait) {
		try {
			return future.get(defaultTimeout, TimeUnit.MILLISECONDS);
		} catch (ExecutionException e) {
			throw WrappedException.wrap(e.getCause());
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public T get() {
		return get(defaultTimeout);
	}

	@Override
	public void cancel() {
		cancelled = true;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

}

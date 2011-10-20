package org.softwareFm.utilities.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;

public class GatedMockFuture<From,To> implements Future<To> {

	private final From from;
	private boolean done;
	private final IFunction1<From, To> function;
	private To result;

	public GatedMockFuture(IFunction1<From, To> function, From from) {
		super();
		this.function = function;
		this.from = from;
	}

	public void kick() {
		try {
			result = function.apply(from);
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
	public To get() throws InterruptedException, ExecutionException {
		if (!done)
			throw new IllegalStateException();
		return result;
	}

	@Override
	public To get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException {
		if (!done)
			throw new IllegalStateException();
		return result;
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

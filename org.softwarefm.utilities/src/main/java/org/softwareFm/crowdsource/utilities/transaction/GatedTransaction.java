package org.softwareFm.crowdsource.utilities.transaction;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public class GatedTransaction<T> implements ITransaction<T> {

	private final CountDownLatch latch = new CountDownLatch(1);
	private final Object from;
	private final IFunction1<?, T> function;
	private T value;

	public GatedTransaction(T value) {
		super();
		this.function = null;
		this.from = null;
		this.value = value;
	}

	public <From> GatedTransaction(IFunction1<From, T> function, From from) {
		super();
		this.function = function;
		this.from = from;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void kick() {
		this.value = function == null ? value : Functions.<Object, T> call((IFunction1) function, from);
		latch.countDown();
	}
	
	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public T get() {
		try {
			latch.await();
			return value;
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public T get(long millisecondsToWait) {
		try {
			if (!latch.await(millisecondsToWait, TimeUnit.MILLISECONDS))
				throw new TimeoutException();
			return value;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void cancel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return latch.getCount() == 0;
	}

}

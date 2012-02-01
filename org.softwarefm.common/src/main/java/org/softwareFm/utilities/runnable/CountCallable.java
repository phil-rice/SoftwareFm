package org.softwareFm.utilities.runnable;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class CountCallable<T> implements Callable<T> {

	private final T value;
	private final AtomicInteger count = new AtomicInteger();

	public CountCallable(T value) {
		super();
		this.value = value;
	}

	@Override
	public T call() throws Exception {
		count.incrementAndGet();
		return value;
	}

	public int getCount() {
		return count.get();
	}

}

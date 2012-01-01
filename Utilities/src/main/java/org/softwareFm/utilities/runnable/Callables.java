package org.softwareFm.utilities.runnable;

import java.util.concurrent.Callable;

public class Callables {

	public static <V> CountCallable<V> count(V value) {
		return new CountCallable<V>(value);
	}

	public static Callable<Long> time() {
		return new Callable<Long>() {
			@Override
			public Long call() throws Exception {
				return System.currentTimeMillis();
			}
		};
	}

	public static <T> Callable<T> value(final T value) {
		return new Callable<T>() {
			@Override
			public T call() throws Exception {
				return value;
			}
		};
	}

}

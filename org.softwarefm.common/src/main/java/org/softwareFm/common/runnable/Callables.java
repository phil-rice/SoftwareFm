package org.softwareFm.common.runnable;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.exceptions.WrappedException;

public class Callables {

	public static Callable<String> uuidGenerator() {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				return UUID.randomUUID().toString();
			}
		};
	}

	public static <V> V call(Callable<V> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

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

	public static Callable<String> patternWithCount(final String pattern) {
		return new Callable<String>() {
			private final AtomicInteger integer = new AtomicInteger();

			@Override
			public String call() throws Exception {
				return MessageFormat.format(pattern, integer.getAndIncrement());
			}
		};
	}

	public static <T> Callable<T> exceptionIfCalled() {
		return new Callable<T>() {
			@Override
			public T call() throws Exception {
				throw new RuntimeException();
			}
		};
	}

	public static Callable<String> makeCryptoKey() {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				return Crypto.makeKey();
			}
		};
	}

}

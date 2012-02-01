package org.softwareFm.utilities.runnable;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.exceptions.WrappedException;

public class Callables {

	public static Callable<String> uuidGenerator(){
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
	public static  Callable<String> patternWithCount(final String pattern) {
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

	public static Callable<String> monthGetter() {
		return new Callable<String>() {
			private final String [] month2String = new String[]{"january", "febuary", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"};
			@Override
			public String call() throws Exception {
				int month = Calendar.getInstance().get(Calendar.MONTH);
				return month2String[month];
			}
		};
	}
	public static Callable<Integer> dayGetter() {
		return new Callable<Integer>(){
			@Override
			public Integer call() throws Exception {
				int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				return day;
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

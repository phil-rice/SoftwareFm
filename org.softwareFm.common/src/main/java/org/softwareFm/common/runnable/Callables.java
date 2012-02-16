/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.runnable;

import java.text.MessageFormat;
import java.util.Calendar;
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
	public static <T> Callable<T> valueFromList(final T... value) {
		return new Callable<T>() {
			private final AtomicInteger index = new AtomicInteger();
			@Override
			public T call() throws Exception {
				return value[index.getAndIncrement()];
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

	public static Callable<Calendar> calander() {
		return new Callable<Calendar>() {
			@Override
			public Calendar call() throws Exception {
				return Calendar.getInstance();
			}
		};
	}

	public static Callable<Calendar> calander(final int year, final int month, final int day, final int hour, final int minute) {
		return new Callable<Calendar>() {
			@Override
			public Calendar call() throws Exception {
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month,day, hour, minute);
				return calendar;
			}
		};
	}

}
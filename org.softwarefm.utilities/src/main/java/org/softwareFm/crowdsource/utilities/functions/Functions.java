/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.functions;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.UtilityMessages;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;

public class Functions {

	public static class ConstantFunctionWithMemoryOfFroms<From, To> implements IFunction1<From, To> {
		private final To object;
		public final List<From> froms = Lists.newList();
		public final List<Thread> threads = Lists.newList();

		public ConstantFunctionWithMemoryOfFroms(To object) {
			this.object = object;
		}

		@Override
		public To apply(From from) throws Exception {
			threads.add(Thread.currentThread());
			froms.add(from);
			return object;
		}
		@Override
		public String toString() {
			return "ConstantFunction(" + object + ")";
		}

	}

	public static <From, To> To call(final IFunction1<From, To> fn, From value) {
		try {
			return fn.apply(value);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <From1, From2, To> To call(final IFunction2<From1, From2, To> fn, From1 value1, From2 value2) {
		try {
			return fn.apply(value1, value2);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <From1, From2, From3, To> To call(final IFunction3<From1, From2, From3, To> fn, From1 value1, From2 value2, From3 value3) {
		try {
			return fn.apply(value1, value2, value3);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <From, To> IFunction1<From, To> expectValueAndReturnConstant(final From expectedFrom, final To to) {
		return new IFunction1<From, To>() {
			@Override
			public To apply(From from) throws Exception {
				if (expectedFrom.equals(from))
					return to;
				throw new IllegalArgumentException(MessageFormat.format(UtilityMessages.expectedButGot, expectedFrom, from));
			}
			@Override
			public String toString() {
				return "expectValueAndReturnConstant(" + to +")";
			}
		};
	}

	public static <From, Middle, To> IFunction1<From, To> compose(final IFunction1<From, Middle> one, final IFunction1<Middle, To> two) {
		return new IFunction1<From, To>() {
			@Override
			public To apply(From from) throws Exception {
				Middle middle = one.apply(from);
				return two.apply(middle);
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public static IFunction1 arraysBecomeLists() {
		return new IFunction1() {
			@Override
			public Object apply(Object from) throws Exception {
				if (from.getClass().isArray()) {
					List<Object> result = Lists.newList();
					for (Object f : (Object[]) from)
						result.add(f);
					return result;
				} else
					return from;
			}
		};
	}

	public static ISymmetricFunction<Double> plus() {
		return new ISymmetricFunction<Double>() {
			@Override
			public Double apply(Double value, Double initial) {
				return value + initial;
			}
		};
	}

	public static ISymmetricFunction<Integer> plusInt() {
		return new ISymmetricFunction<Integer>() {
			@Override
			public Integer apply(Integer value, Integer initial) {
				return value + initial;
			}
		};
	}

	public static IFunction1<Double, Double> times(final double by) {
		return new IFunction1<Double, Double>() {
			@Override
			public Double apply(Double from) throws Exception {
				return from * by;
			}
		};
	}

	public static IFunction1<Double, Double> divide(final double by) {
		return new IFunction1<Double, Double>() {
			@Override
			public Double apply(Double from) throws Exception {
				return from / by;
			}
		};
	}

	public static IFunction1<Integer, Integer> timesInt(final int by) {
		return new IFunction1<Integer, Integer>() {
			@Override
			public Integer apply(Integer from) throws Exception {
				return from * by;
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public static IFunction1 identity = new IFunction1() {
		@Override
		public Object apply(Object from) throws Exception {
			return from;
		}
	};

	@SuppressWarnings("unchecked")
	public static <T1, T2> IFunction1<T1, T2> identity() {
		return identity;
	}

	@SuppressWarnings("unchecked")
	public static <T1, T2> IFunction1<T1, T2> cast() {
		return identity;
	}

	@SuppressWarnings("rawtypes")
	public static IFunction1 toStringFn = new IFunction1() {
		@Override
		public Object apply(Object from) throws Exception {
			return from.toString();
		}
	};

	@SuppressWarnings("unchecked")
	public static <T> IFunction1<T, String> toStringFn() {
		return toStringFn;
	}

	public static IFunction1<Integer, Double> intToDouble() {
		return new IFunction1<Integer, Double>() {
			@Override
			public Double apply(Integer from) throws Exception {
				return from.doubleValue();
			}
		};
	}

	public static IFunction1<Integer, Boolean> even() {
		return new IFunction1<Integer, Boolean>() {
			@Override
			public Boolean apply(Integer from) throws Exception {
				return from % 2 == 0;
			}
		};
	}

	public static IFunction1<Integer, Boolean> odd() {
		return new IFunction1<Integer, Boolean>() {
			@Override
			public Boolean apply(Integer from) throws Exception {
				return from % 2 != 0;
			}
		};
	}

	public static <T> IFunction1<T, String> addToEnd(final String string) {
		return new IFunction1<T, String>() {
			@Override
			public String apply(T from) throws Exception {
				return from + string;
			}
		};
	}

	public static IFunction1<String, String> addToStart(final String string) {
		return new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return string + from;
			}
		};
	}

	public static <T> IFunction1<T, Boolean> isNull() {
		return new IFunction1<T, Boolean>() {
			@Override
			public Boolean apply(T from) throws Exception {
				return from == null;
			}
		};
	}

	public static <T> IFunction1<T, Boolean> trueFn() {
		return new IFunction1<T, Boolean>() {
			@Override
			public Boolean apply(T from) throws Exception {
				return true;
			}
		};
	}

	public static <T> IFunction1<T, Boolean> falseFn() {
		return new IFunction1<T, Boolean>() {
			@Override
			public Boolean apply(T from) throws Exception {
				return false;
			}
		};
	}

	public static <T> IFunction1<T, Boolean> and(final IFunction1<T, Boolean>... fns) {
		return new IFunction1<T, Boolean>() {
			@Override
			public Boolean apply(T from) throws Exception {
				for (IFunction1<T, Boolean> fn : fns)
					if (!fn.apply(from))
						return false;
				return true;
			}
		};
	}

	public static <K1, K2> IFunction1<K1, List<K2>> toSingletonList() {
		return new IFunction1<K1, List<K2>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<K2> apply(K1 from) throws Exception {
				return (List<K2>) Arrays.asList(from);
			}
		};
	}

	public static <T> IFunction1<T, Class<?>> toClass() {
		return new IFunction1<T, Class<?>>() {
			@Override
			public Class<?> apply(T from) throws Exception {
				return from.getClass();
			}
		};
	}

	public static <From, To> IFunction1<From, To> constant(final To object) {
		return new IFunction1<From, To>() {
			@Override
			public To apply(From from) throws Exception {
				return object;
			}

			@Override
			public String toString() {
				return "Constant: " + object;
			}
		};
	}

	public static <From, To> ConstantFunctionWithMemoryOfFroms<From, To> constantWithMemoryOfFrom(final To object) {
		return new ConstantFunctionWithMemoryOfFroms<From, To>(object);
	}

	public static <To> ConstantFunctionWithMemoryOfFroms<IMonitor, To> constantWithMemoryOfMonitor(final To object) {
		return new ConstantFunctionWithMemoryOfFroms<IMonitor, To>(object) {
			@Override
			public To apply(IMonitor from) throws Exception {
				from.beginTask("someTask", 1);
				return super.apply(from);
			}
		};
	}

	public static <From, To> IFunction1<From, To> expectionIfCalled() {
		return new IFunction1<From, To>() {
			@Override
			public To apply(From from) throws Exception {
				throw new RuntimeException();
			}
		};
	}

	public static <From, To> IFunction1<From, To> expectionIfCalled(final Exception e) {
		return new IFunction1<From, To>() {
			@Override
			public To apply(From from) throws Exception {
				throw e;
			}
		};
	}

	public static IFunction1<String, Integer> stringToIntegerFn() {
		return new IFunction1<String, Integer>() {
			@Override
			public Integer apply(String from) throws Exception {
				return Integer.parseInt(from);
			}
		};
	}

	public static IFunction1<Integer, Integer> doubleInts() {
		return new IFunction1<Integer, Integer>() {

			@Override
			public Integer apply(Integer from) throws Exception {
				return from * 2;
			}
		};
	}

	public static <From, To> IFunction1<From, To> map(final Object... nameAndAttributes) {
		return new IFunction1<From, To>() {
			private final Map<From, To> map = Maps.makeMap(nameAndAttributes);

			@Override
			public To apply(From from) throws Exception {
				return map.get(from);
			}
		};
	}

	public static IFunction1<Map<String, Object>, String> mapFromKey(final String key, final Object... namesAndValues) {
		return new IFunction1<Map<String, Object>, String>() {
			private final Map<String, Object> map = Maps.stringObjectMap(namesAndValues);

			@Override
			public String apply(Map<String, Object> from) throws Exception {
				Object actualKey = from.get(key);
				return (String) map.get(actualKey);
			}
		};
	}

	public static <K, V> IFunction1<Map<K, V>, V> get(final K key) {
		return new IFunction1<Map<K, V>, V>() {
			@Override
			public V apply(Map<K, V> from) throws Exception {
				return from.get(key);
			}
		};
	}

	public static IFoldFunction<Boolean, Boolean> or() {
		return new IFoldFunction<Boolean, Boolean>() {
			@Override
			public Boolean apply(Boolean value, Boolean initial) {
				return value || initial;
			}
		};
	}

	public static IFoldFunction<Boolean, Boolean> and() {
		return new IFoldFunction<Boolean, Boolean>() {
			@Override
			public Boolean apply(Boolean value, Boolean initial) {
				return value && initial;
			}
		};
	}

	public static IFunction1<IMonitor, String> exceptionIfCalled(final RuntimeException runtimeException) {
		return new IFunction1<IMonitor, String>() {
			@Override
			public String apply(IMonitor from) throws Exception {
				throw runtimeException;
			}
		};
	}

	public static <From> IFunction1<From, Integer> ensureSameParameters() {
		return new EnsureSameParamaters<From>();

	}

	public static <From1, From2, To> IFunction2<From1, From2, To> ensureSameParameters2() {
		return new IFunction2<From1, From2, To>() {
			private From1 value1;
			private From2 value2;

			@Override
			public To apply(From1 from1, From2 from2) throws Exception {
				Assert.assertNotNull(from1);
				Assert.assertNotNull(from2);
				if (value1 == null)
					value1 = from1;
				else
					Assert.assertSame(value1, from1);
				if (value2 == null)
					value2 = from2;
				else
					Assert.assertSame(value2, from2);
				return null;
			}
		};
	}

	static final class EnsureSameParamaters<From> implements IFunction1<From, Integer> {
		private From value;
		public final AtomicInteger count = new AtomicInteger();

		@Override
		public Integer apply(From from) throws Exception {
			Assert.assertNotNull(from);
			if (value == null)
				value = from;
			else
				Assert.assertSame(value, from);
			return count.incrementAndGet();
		}
	}

	public static <T> IFunction1<T, T> identityWithCallback(final ICallback<T> callback) {
		return new IFunction1<T, T>() {
			@Override
			public T apply(T from) throws Exception {
				callback.process(from);
				return from;
			}
		};
	}
}
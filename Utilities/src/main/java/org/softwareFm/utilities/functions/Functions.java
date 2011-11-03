package org.softwareFm.utilities.functions;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;

public class Functions {

	public static <From, To> To call(final IFunction1<From, To> fn, From value) {
		try {
			return fn.apply(value);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

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

	public static IFunction1<String, String> addToEnd(final String string) {
		return new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
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
		};
	}

	public static <From,To>IFunction1<From,To> expectionIfCalled() {
		return new IFunction1<From, To>() {
			@Override
			public To apply(From from) throws Exception {
				throw new RuntimeException();
			}
		};
	}
}

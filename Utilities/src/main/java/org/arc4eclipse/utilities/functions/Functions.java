package org.arc4eclipse.utilities.functions;

public class Functions {

	public static <From, Middle, To> IFunction1<From, To> compose(final IFunction1<From, Middle> one, final IFunction1<Middle, To> two) {
		return new IFunction1<From, To>() {
			public To apply(From from) throws Exception {
				Middle middle = one.apply(from);
				return two.apply(middle);
			}
		};
	}

	public static ISymmetricFunction<Double> plus() {
		return new ISymmetricFunction<Double>() {
			public Double apply(Double value, Double initial) {
				return value + initial;
			}
		};
	}

	public static ISymmetricFunction<Integer> plusInt() {
		return new ISymmetricFunction<Integer>() {
			public Integer apply(Integer value, Integer initial) {
				return value + initial;
			}
		};
	}

	public static IFunction1<Double, Double> times(final double by) {
		return new IFunction1<Double, Double>() {
			public Double apply(Double from) throws Exception {
				return from * by;
			}
		};
	}

	public static IFunction1<Double, Double> divide(final double by) {
		return new IFunction1<Double, Double>() {
			public Double apply(Double from) throws Exception {
				return from / by;
			}
		};
	}

	public static IFunction1<Integer, Integer> timesInt(final int by) {
		return new IFunction1<Integer, Integer>() {
			public Integer apply(Integer from) throws Exception {
				return from * by;
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public static IFunction1 identity = new IFunction1() {
		public Object apply(Object from) throws Exception {
			return from;
		}
	};

	@SuppressWarnings("unchecked")
	public static <T1, T2> IFunction1<T1, T2> identity() {
		return identity;
	}

	@SuppressWarnings("rawtypes")
	public static IFunction1 toStringFn = new IFunction1() {
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
			public Double apply(Integer from) throws Exception {
				return from.doubleValue();
			}
		};
	}

	public static IFunction1<Integer, Boolean> even() {
		return new IFunction1<Integer, Boolean>() {
			public Boolean apply(Integer from) throws Exception {
				return from % 2 == 0;
			}
		};
	}

	public static IFunction1<Integer, Boolean> odd() {
		return new IFunction1<Integer, Boolean>() {
			public Boolean apply(Integer from) throws Exception {
				return from % 2 != 0;
			}
		};
	}

	public static IFunction1<String, String> append(final String string) {
		return new IFunction1<String, String>() {
			public String apply(String from) throws Exception {
				return from + string;
			}
		};
	}
}

package org.softwareFm.utilities.comparators;

import java.util.Comparator;
import java.util.Map;

public class Comparators {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Comparator naturalComparator = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			Comparable one = (Comparable) o1;
			Comparable two = (Comparable) o2;
			return one.compareTo(two);
		}
	};

	@SuppressWarnings("unchecked")
	public static <T> Comparator<T> naturalOrder() {
		return naturalComparator;
	}

	public static <T> Comparator<T> invert(final Comparator<T> raw) {
		return new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return raw.compare(o2, o1);
			}
		};
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Comparator<String> compareBasedOnTagInValue(final Map<String, Object> map, final String tag) {
		return new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				Object left = get(o1);
				Object right = get(o2);
				if (left == null)
					if (right == null)
						return 0;
					else
						return -1;
				else if (right == null)
					return 1;
				else
					return naturalComparator.compare(left, right);
			}

			private Object get(String key) {
				Object object = map.get(key);
				if (object instanceof Map<?, ?>)
					return ((Map) object).get(tag);
				return null;
			}
		};
	}
}

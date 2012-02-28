/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.comparators;

import java.util.Comparator;
import java.util.Map;

public class Comparators {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Comparator naturalComparator = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			Comparable one = (Comparable) o1;
			Comparable two = (Comparable) o2;
			if (one == null)
				if (two == null)
					return 0;
				else
					return -1;
			else if (two == null)
				return 1;
			else
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

	public static <K, V> Comparator<Map<K, V>> mapKey(final K key) {
		return new Comparator<Map<K, V>>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(Map<K, V> o1, Map<K, V> o2) {
				V value1 = o1.get(key);
				V value2 = o2.get(key);
				return naturalComparator.compare(value1, value2);
			}
		};
	}
}
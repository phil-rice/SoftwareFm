package org.softwareFm.card.api;

import java.util.Comparator;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.strings.Strings;

public class KeyValue {
	public final String key;
	public final Object value;

	public static class Utils {
		public static IFunction1<KeyValue, String> keyFn() {
			return new IFunction1<KeyValue, String>() {
				@Override
				public String apply(KeyValue from) throws Exception {
					return from.key;
				}
			};
		}

		public static Comparator<KeyValue> orderedKeyComparator(String... order) {
			return Lists.comparator(keyFn(), Lists.orderedComparator(order));
		}

		public static IFunction1<KeyValue, String> valueAsStrFn() {
			return new IFunction1<KeyValue, String>() {

				@Override
				public String apply(KeyValue from) throws Exception {
					return Strings.nullSafeToString(from.value);
				}
			};
		}
	}

	public KeyValue(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return "KeyValue [key=" + key + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyValue other = (KeyValue) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}

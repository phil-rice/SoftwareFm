/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card;

import java.util.Comparator;

import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.strings.Strings;

/** Data about a single line in the card */
public class LineItem {
	public final String cardType;
	public final String key;
	public final Object value;

	public static class Utils {
		public static IFunction1<LineItem, String> keyFn() {
			return new IFunction1<LineItem, String>() {
				@Override
				public String apply(LineItem from) throws Exception {
					return from.key;
				}
			};
		}

		public static ILineItemFunction<String> keyLineItemFn() {
			return new ILineItemFunction<String>() {
				@Override
				public String apply(CardConfig cardConfig, LineItem from) {
					return from.key;
				}
			};
		}

		public static Comparator<LineItem> orderedKeyComparator(String... order) {
			return Lists.comparator(keyFn(), Lists.orderedComparator(order));
		}

		public static ILineItemFunction<String> valueAsStrLineItemFn() {
			return new ILineItemFunction<String>() {
				@Override
				public String apply(CardConfig cardConfig, LineItem item) {
					return Strings.nullSafeToString(item.value);
				}
			};
		}
	}

	public LineItem(String cardType, String key, Object value) {
		this.cardType = cardType;
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return "LineItem [key=" + key + ", value=" + value + ", cardType=" + cardType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardType == null) ? 0 : cardType.hashCode());
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
		LineItem other = (LineItem) obj;
		if (cardType == null) {
			if (other.cardType != null)
				return false;
		} else if (!cardType.equals(other.cardType))
			return false;
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
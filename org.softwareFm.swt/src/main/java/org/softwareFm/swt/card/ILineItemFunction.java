/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card;

import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.swt.card.internal.CardNameFunction;
import org.softwareFm.swt.card.internal.CardValueFunction;
import org.softwareFm.swt.card.internal.LineItemHideFunction;
import org.softwareFm.swt.configuration.CardConfig;

public interface ILineItemFunction<T> {
	T apply(CardConfig cardConfig, LineItem item);

	public static class Utils {
		public static ILineItemFunction<String> nameFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String namePattern) {
			return new CardNameFunction(resourceGetterFn, namePattern);
		}

		public static ILineItemFunction<String> valueFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String valuePattern) {
			return new CardValueFunction(resourceGetterFn, valuePattern);
		}

		public static ILineItemFunction<Boolean> hideFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String key) {
			return new LineItemHideFunction(resourceGetterFn, key);
		}

		public static ILineItemFunction<Boolean> falseFn() {
			return new ILineItemFunction<Boolean>() {
				@Override
				public Boolean apply(CardConfig cardConfig, LineItem item) {
					return false;
				}
			};
		}
	}

}
package org.softwareFm.card.card;

import org.softwareFm.card.card.internal.CardNameFunction;
import org.softwareFm.card.card.internal.CardValueFunction;
import org.softwareFm.card.card.internal.LineItemHideFunction;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public interface ILineItemFunction<T> {
	T apply(CardConfig cardConfig, LineItem item) ;

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
			return new ILineItemFunction<Boolean>(){
				@Override
				public Boolean apply(CardConfig cardConfig, LineItem item) {
					return false;
				}};
		}
	}

}
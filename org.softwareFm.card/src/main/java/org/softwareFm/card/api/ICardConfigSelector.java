package org.softwareFm.card.api;

import java.util.Map;

public interface ICardConfigSelector {

	CardConfig getConfigFor(Map<String, Object> map);

	public static class Utils {

		public static ICardConfigSelector defaultSelector(final ICardFactory cardFactory, final ICardDataStore cardDataStore) {
			return defaultSelector(new CardConfig(cardFactory, cardDataStore));
		}

		public static ICardConfigSelector defaultSelector(final CardConfig cardConfig) {
			return new ICardConfigSelector() {
				@Override
				public CardConfig getConfigFor(Map<String, Object> map) {
					return cardConfig;
				}
			};
		}
	}
}

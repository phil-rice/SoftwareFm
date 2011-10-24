package org.softwareFm.card.api;

import org.softwareFm.card.internal.CardAndCollectionsStatus;

public interface ICardAndCollectionsDataStore {

	/** Note that the cardholder is populated early, but the card may get prodded with valueHasChanged, and the future isn't done until all values have been changed */
	CardAndCollectionsStatus processDataFor(final ICardHolder cardHolder, final CardConfig cardConfig, final String url, CardAndCollectionDataStoreVisitorMock whenFinished);
}

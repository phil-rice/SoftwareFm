package org.softwareFm.card.dataStore;

import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICardHolder;

/** Gets data about a card, and the may ask for follow up data as well.*/
public interface ICardAndCollectionsDataStore {

	/** Note that the cardholder is populated early, but the card may get prodded with valueHasChanged, and the future isn't done until all values have been changed */
	CardAndCollectionsStatus processDataFor(final ICardHolder cardHolder, final CardConfig cardConfig, final String url, ICardAndCollectionDataStoreVisitor whenFinished);
}

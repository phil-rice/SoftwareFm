package org.softwareFm.card.api;

import java.util.Map;

import org.softwareFm.card.dataStore.ICardAndCollectionDataStoreVisitor;

public class CardAndCollectionDataStoreVisitorMonitored implements ICardAndCollectionDataStoreVisitor {

	public int initialUrlCount;
	public int initialCardCount;
	public int requestingFollowUpCount;
	public int followedUpCount;
	public int noDataCount;
	public int finishedCount;

	@Override
	public void initialUrl(ICardHolder cardHolder, CardConfig cardConfig, String url) {
		initialUrlCount++;

	}

	@Override
	public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card) {
		initialCardCount++;
	}

	@Override
	public void requestingFollowup(ICardHolder cardHolder, String url, ICard card, String followOnUrlFragment) {
		requestingFollowUpCount++;
	}

	@Override
	public void followedUp(ICardHolder cardHolder, String url, ICard card, String followUpUrl, Map<String, Object> result) {
		followedUpCount++;
	}

	@Override
	public void noData(ICardHolder cardHolder, String url, ICard card, String followUpUrl) {
		noDataCount++;
	}

	@Override
	public void finished(ICardHolder cardHolder, String url, ICard card) {
		finishedCount++;
	}

}

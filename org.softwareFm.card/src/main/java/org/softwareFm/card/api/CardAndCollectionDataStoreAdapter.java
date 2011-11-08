package org.softwareFm.card.api;

import java.util.Map;

public class CardAndCollectionDataStoreAdapter implements ICardAndCollectionDataStoreVisitor {

	@Override
	public void initialUrl(ICardHolder cardHolder, CardConfig cardConfig, String url) {
	}

	@Override
	public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card) {
	}

	@Override
	public void requestingFollowup(ICardHolder cardHolder, String url, ICard card, String followOnUrlFragment) {
	}

	@Override
	public void followedUp(ICardHolder cardHolder, String url, ICard card, String followUpUrl, Map<String, Object> result) {
	}

	@Override
	public void noData(ICardHolder cardHolder, String url, ICard card, String followUpUrl) {
	}

	@Override
	public void finished(ICardHolder cardHolder, String url, ICard card) {
	}

}

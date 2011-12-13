package org.softwareFm.collections.explorer;

import java.util.Map;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;

public class ExplorerAdapter implements IExplorerListener {

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
	public void finished(ICardHolder cardHolder, String url, ICard card) throws Exception {
	}

	@Override
	public void showContents(ICard card) {
	}

	@Override
	public void displayCard(String url) {
	}

	@Override
	public void commentAdded(String commentsUrl, String key) {
	}

	@Override
	public void collectionItemAdded(String collectionUrl, String key) {
	}

}

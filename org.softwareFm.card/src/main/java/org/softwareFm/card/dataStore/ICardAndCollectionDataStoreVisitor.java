package org.softwareFm.card.dataStore;

import java.util.Map;

import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;

/** The life cycle for requesting more data about a card is monitored by this visitor */
public interface ICardAndCollectionDataStoreVisitor {

	/** The initial data is being requested */
	void initialUrl(ICardHolder cardHolder, CardConfig cardConfig, String url);

	/** The initial data has been found and turned into a card */
	void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card);

	/** More data is needed from one of the child nodes */
	void requestingFollowup(ICardHolder cardHolder, String url, ICard card, String followOnUrlFragment);

	/** More data has been found */
	void followedUp(ICardHolder cardHolder, String url, ICard card, String followUpUrl, Map<String, Object> result);

	/** There was no initial data for the card */
	void noData(ICardHolder cardHolder, String url, ICard card, String followUpUrl);

	/** All the data, and the follow up data has been found */
	void finished(ICardHolder cardHolder, String url, ICard card);

	public static class Utils {
		public static CardAndCollectionDataStoreVisitorMonitored noVisitor() {
			return new CardAndCollectionDataStoreVisitorMonitored() {
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
			};
		}

		public static CardAndCollectionDataStoreVisitorMonitored sysout() {
			return new CardAndCollectionDataStoreVisitorMonitored() {
				private int h(Object object) {
					return object.hashCode() % 1000;
				}

				@Override
				public void initialUrl(ICardHolder cardHolder, CardConfig cardConfig, String url) {
					System.out.println(String.format("%5d Requesting %40s", h(cardHolder), url));
				}

				@Override
				public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card) {
					System.out.println(String.format("%5d    Initial   %40s   %s", h(cardHolder), url, card.data().toString()));
				}

				@Override
				public void requestingFollowup(ICardHolder cardHolder, String url, ICard card, String followOnUrlFragment) {
					System.out.println(String.format("%5d       More    %40s   %s", h(cardHolder), url, followOnUrlFragment));
				}

				@Override
				public void followedUp(ICardHolder cardHolder, String url, ICard card, String followUpUrl, Map<String, Object> result) {
					System.out.println(String.format("%5d       Have More %40s   %s", h(cardHolder), url, result.toString()));
				}

				@Override
				public void noData(ICardHolder cardHolder, String url, ICard card, String followUpUrl) {
					System.out.println(String.format("%5d       No Data    %40s", h(cardHolder), url));
				}

				@Override
				public void finished(ICardHolder cardHolder, String url, ICard card) {
					System.out.println(String.format("%5d Finished    %40s  %s", h(cardHolder), url, card.data().toString()));
				}
			};
		}
	}

}

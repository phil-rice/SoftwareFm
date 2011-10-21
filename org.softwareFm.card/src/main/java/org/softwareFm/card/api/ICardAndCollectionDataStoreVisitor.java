package org.softwareFm.card.api;

import java.util.Map;

public interface ICardAndCollectionDataStoreVisitor {

	void initialUrl(ICardHolder cardHolder, CardConfig cardConfig, String url);

	void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card);

	void requestingFollowup(ICardHolder cardHolder, String url, ICard card, String followOnUrlFragment);

	void followedUp(ICardHolder cardHolder, String url, ICard card, String followUpUrl, Map<String, Object> result);

	void noData(ICardHolder cardHolder, String url, ICard card, String followUpUrl);

	void finished(ICardHolder cardHolder, String url, ICard card);

	public static class Utils {
		public static ICardAndCollectionDataStoreVisitor noVisitor() {
			return new ICardAndCollectionDataStoreVisitor() {
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

		public static ICardAndCollectionDataStoreVisitor sysout() {
			return new ICardAndCollectionDataStoreVisitor() {
				private int h(Object object) {
					return object.hashCode() % 1000;
				}

				@Override
				public void initialUrl(ICardHolder cardHolder, CardConfig cardConfig, String url) {
					System.out.println(String.format("%5d Requesting %40s", h(cardHolder), url));
				}

				@Override
				public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card) {
					System.out.println(String.format("%5d    Initial   %40s   %s", h(cardHolder), url, card.rawData().toString()));
				}

				@Override
				public void requestingFollowup(ICardHolder cardHolder, String url, ICard card, String followOnUrlFragment) {
					System.out.println(String.format("%5d       More    %40s   %s", h(cardHolder), url, followOnUrlFragment));
				}

				@Override
				public void followedUp(ICardHolder cardHolder, String url, ICard card, String followUpUrl, Map<String, Object> result) {
					System.out.println(String.format("%5d       Have More %40s   %s", h(cardHolder), url, result.toString()));}

				@Override
				public void noData(ICardHolder cardHolder, String url, ICard card, String followUpUrl) {
					System.out.println(String.format("%5d       No Data    %40s", h(cardHolder), url));
				}

				@Override
				public void finished(ICardHolder cardHolder, String url, ICard card) {
					System.out.println(String.format("%5d Finished    %40s  %s", h(cardHolder), url, card.rawData().toString()));
				}
			};
		}
	}

}

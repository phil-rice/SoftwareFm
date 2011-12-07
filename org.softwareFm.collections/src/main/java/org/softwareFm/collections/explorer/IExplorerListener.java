package org.softwareFm.collections.explorer;

import java.util.Map;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.ICardAndCollectionDataStoreVisitor;

public interface IExplorerListener extends ICardAndCollectionDataStoreVisitor {

	void showContents(ICard card);

	void displayCard(String url);
	
	static class Utils{
		public static IExplorerListener sysout(){
			return new IExplorerListener() {
				
				@Override
				public void requestingFollowup(ICardHolder cardHolder, String url, ICard card, String followOnUrlFragment) {
					System.out.println("  requestingFollowup: " + url+", " + followOnUrlFragment);
				}
				
				@Override
				public void noData(ICardHolder cardHolder, String url, ICard card, String followUpUrl) {
					System.out.println("  noData: " + url +", " + followUpUrl);
				}
				
				@Override
				public void initialUrl(ICardHolder cardHolder, CardConfig cardConfig, String url) {
					System.out.println("initialUrl: " + url);
				}
				
				@Override
				public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card) {
					System.out.println("initialCard: " + url+", " + card);
				}
				
				@Override
				public void followedUp(ICardHolder cardHolder, String url, ICard card, String followUpUrl, Map<String, Object> result) {
					System.out.println("  followedUp: " + url + ", " + followUpUrl +", " + result);
				}
				
				@Override
				public void finished(ICardHolder cardHolder, String url, ICard card) throws Exception {
					System.out.println("finished: " + url + ", " + card);
				}
				
				@Override
				public void showContents(ICard card) {
					System.out.println("showContents: " + card);
				}
				
				@Override
				public void displayCard(String url) {
					System.out.println("displayCard: " + url);
				}
			};
			
		}
	}

}
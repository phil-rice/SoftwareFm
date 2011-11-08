package org.softwareFm.explorer.eclipse;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.internal.BasicCardConfigurator;
import org.softwareFm.card.internal.CardDataStoreForRepository;
import org.softwareFm.display.browser.BrowserFeedConfigurator;
import org.softwareFm.display.browser.RssFeedConfigurator;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.services.IServiceExecutor;

public class ExplorerUnit {

	public static void main(String[] args) {
		final IServiceExecutor service = IServiceExecutor.Utils.defaultExecutor();
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		try {
			final String rootUrl = "/softwareFm/data";
			Swts.xUnit(ExplorerUnit.class.getSimpleName(), new ISituationListAndBuilder<Explorer>() {
				@Override
				public void selected(Explorer hasControl, String context, Object value) throws Exception {
					Map<String, Object> map = (Map<String, Object>) value;
					String master = (String) map.get("card");
					Map<String, String> jar = (Map<String, String>) map.get("jar");
					Map<String, String> browser = (Map<String, String>) map.get("browser");

					String social = (String) map.get("social");

					if (master != null)
						hasControl.displayCard(master);
					if (jar != null)
						hasControl.displayUnrecognisedJar(jar.get("url"), jar.get("file"));
					if (browser != null)
						hasControl.processUrl(browser.get("feedType"), browser.get("url"));
					if (social != null)
						hasControl.displayComments(social);
				}

				@Override
				public Explorer makeChild(Composite parent) throws Exception {
					final ICardDataStore cardDataStore = new CardDataStoreForRepository(parent, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = new BasicCardConfigurator().configure(parent.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IMasterDetailSocial masterDetailSocial = new MasterDetailSocial(parent, SWT.NULL);
					Explorer explorer = new Explorer(cardConfig, rootUrl, masterDetailSocial, service);
					new BrowserFeedConfigurator().configure(null, explorer);
					new RssFeedConfigurator().configure(null, explorer);
					return explorer;
				}
			}, Maps.stringObjectLinkedMap(//
					"org/apache/ant", Maps.makeMap(//
							"card", "/softwareFm/data/org/apache/ant/org.apache.ant", //
							"browser", Maps.makeMap("feedType", "feedType.rss", "url", "http://feeds.bbci.co.uk/news/rss.xml"),
							"social", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"),//
					"card - org/apache", Maps.makeMap("card", "/softwareFm/data/org/apache")//

			));
		} finally {
			facard.shutdown();
			service.shutdown();
		}
	}
}

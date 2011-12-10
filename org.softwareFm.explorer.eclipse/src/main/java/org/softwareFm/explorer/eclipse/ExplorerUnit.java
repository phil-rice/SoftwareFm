/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.explorer.eclipse;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.explorer.IMasterDetailSocial;
import org.softwareFm.display.browser.BrowserFeedConfigurator;
import org.softwareFm.display.browser.RssFeedConfigurator;
import org.softwareFm.display.browser.TweetFeedConfigurator;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.services.IServiceExecutor;

public class ExplorerUnit {

	public static void main(String[] args) {
		final IServiceExecutor service = IServiceExecutor.Utils.defaultExecutor();
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		try {
			final String rootUrl = "/softwareFm/data";
			Show.xUnit(ExplorerUnit.class.getSimpleName(), new ISituationListAndBuilder<IExplorer>() {
				@Override
				@SuppressWarnings("unchecked")
				public void selected(IExplorer explorer, String context, Object value) throws Exception {
					Map<String, Object> map = (Map<String, Object>) value;
					String master = (String) map.get("card");
					Map<String, String> jar = (Map<String, String>) map.get("jar");
					Map<String, String> browser = (Map<String, String>) map.get("browser");

					String social = (String) map.get("social");

					if (master != null)
						explorer.displayCard(master, new CardAndCollectionDataStoreAdapter());
					if (jar != null)
						explorer.displayUnrecognisedJar(new File(jar.get("jar")), jar.get("digest"));
					if (browser != null)
						explorer.processUrl(browser.get("feedType"), browser.get("url"));
					if (social != null)
						explorer.displayHelpText(social);
					Swts.layoutDump(explorer.getControl());
				}

				@Override
				public IExplorer makeChild(Composite parent) throws Exception {
					final ICardDataStore cardDataStore = ICardDataStore.Utils.repositoryCardDataStore(parent, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().configure(parent.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IMasterDetailSocial masterDetailSocial = IMasterDetailSocial.Utils.masterDetailSocial(parent);
					IPlayListGetter noPlayListGetter = IPlayListGetter.Utils.noPlayListGetter();
					IExplorer explorer = IExplorer.Utils.explorer(masterDetailSocial, cardConfig, rootUrl, noPlayListGetter, service);
					new BrowserFeedConfigurator().configure(explorer);
					new RssFeedConfigurator().configure(explorer);
					new TweetFeedConfigurator().configure(explorer);
					Size.resizeMeToParentsSize(masterDetailSocial.getControl());
					return explorer;
				}
			}, Maps.stringObjectLinkedMap(//
					"ant", Maps.makeMap(//
							"card", "/softwareFm/data/org/apache/ant/org.apache.ant", //
							"browser", Maps.makeMap("feedType", "feedType.rss", "url", "http://feeds.bbci.co.uk/news/rss.xml")),//
					"ant+social", Maps.makeMap(//
							"card", "/softwareFm/data/org/apache/ant/org.apache.ant", //
							"browser", Maps.makeMap("feedType", "feedType.rss", "url", "http://feeds.bbci.co.uk/news/rss.xml"),//
							"social", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"),//
					"unrecog - rt", Maps.makeMap(//
							"jar", Maps.makeMap("jar", "/a/b/c/rt.jar", "digest", "0123456"),//
							"social", "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat"),//
					"unrecog - tweet", Maps.makeMap(//
							"jar", Maps.makeMap("jar", "/a/b/c/tweet-4j-1.0.2.jar", "digest", "0123456"),//
							"social", "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat")));//

		} finally {
			facard.shutdown();
			service.shutdown();
		}
	}
}
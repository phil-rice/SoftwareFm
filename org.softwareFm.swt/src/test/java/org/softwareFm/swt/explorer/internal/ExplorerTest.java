/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer.internal;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.resources.ResourceGetterMock;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.eclipse.usage.internal.ApiAndSwtTest;
import org.softwareFm.jarAndClassPath.api.IUserDataManager;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.IShowMyPeople;
import org.softwareFm.swt.login.ILoginStrategy;
import org.softwareFm.swt.menu.PopupMenuContributorMock;
import org.softwareFm.swt.timeline.IPlayListGetter;
import org.softwareFm.swt.title.TitleSpec;

public class ExplorerTest extends ApiAndSwtTest {

	private final static String popupMenuId = "some";
	private CardDataStoreMock updateStore;
	private CardConfig raw;
	private MasterDetailSocial masterDetailSocial;

	public void testExplorerUsesPopupMenuId() {
		Explorer explorer = makeExplorer("withCardNameField");
		final AtomicInteger count = new AtomicInteger();

		explorer.displayCard(CardDataStoreFixture.url1a, new CardAndCollectionDataStoreAdapter() {
			@Override
			public void finished(ICardHolder cardHolder, String url, ICard card) {
				PopupMenuContributorMock<ICard> contributor = new PopupMenuContributorMock<ICard>();
				card.getCardConfig().popupMenuService.registerContributor(popupMenuId, contributor);
				assertEquals(1, count.incrementAndGet());
				Event event = new Event();
				card.getTable().notifyListeners(SWT.MenuDetect, event);
				assertEquals(event, Lists.getOnly(contributor.events));
			}
		});
		assertEquals(1, count.get());
	}

	private Explorer makeExplorer(String cardNameUrl) {
		CardConfig cardConfig = raw.withResourceGetterFn(IResourceGetter.Utils.mock(//
				Functions.call(raw.resourceGetterFn, null).with(new ResourceGetterMock(CardConstants.cardNameUrlKey, cardNameUrl)), // default
				"noCardNameField", new ResourceGetterMock(), //
				"withCardNameField", new ResourceGetterMock(CardConstants.cardNameFieldKey, "cardName"))).//
				withTitleSpecFn(Functions.<ICardData, TitleSpec> constant(TitleSpec.noTitleSpec(shell.getBackground())));
		Explorer explorer = new Explorer(getLocalApi().makeReadWriter(), cardConfig, CardDataStoreFixture.urlAsList, masterDetailSocial, getServiceExecutor(), //
				IPlayListGetter.Utils.noPlayListGetter(),//
				ILoginStrategy.Utils.noLoginStrategy(), IShowMyData.Utils.exceptionShowMyData(), IShowMyGroups.Utils.exceptionShowMyGroups(), //
				IShowMyPeople.Utils.exceptionShowMyPeople(), //
				IUserDataManager.Utils.userDataManager(),//
				Callables.value(1000l)) {
			@Override
			protected String makeRandomUUID() {
				return "randomUUID";
			}
		};
		return explorer;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		updateStore = new CardDataStoreMock();
		masterDetailSocial = new MasterDetailSocial(shell, SWT.NULL);
		raw = IExplorerTest.addNeededResources(new CardConfig(ICardFactory.Utils.cardFactory(), updateStore));
		raw.popupMenuService.registerMenuId(popupMenuId);
		getServerApi().getServer();
	}
}
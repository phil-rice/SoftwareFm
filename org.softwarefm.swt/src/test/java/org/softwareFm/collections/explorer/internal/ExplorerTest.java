/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer.internal;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.dataStore.CardDataStoreMock;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.resources.ResourceGetterMock;
import org.softwareFm.display.menu.PopupMenuContributorMock;
import org.softwareFm.display.swt.SwtAndServiceTest;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.configuration.CardConfig;

public class ExplorerTest extends SwtAndServiceTest {

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
		Explorer explorer = new Explorer(cardConfig, CardDataStoreFixture.urlAsList, masterDetailSocial, service, IPlayListGetter.Utils.noPlayListGetter(), ILoginStrategy.Utils.noLoginStrategy()) {
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
	}
}
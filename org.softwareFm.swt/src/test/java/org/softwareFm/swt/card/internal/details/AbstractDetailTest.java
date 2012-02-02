/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal.details;

import java.util.Collections;

import org.eclipse.swt.graphics.Image;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1WithDispose;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.images.BasicImageRegisterConfigurator;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.card.dataStore.CardDataStoreAsyncMock;
import org.softwareFm.swt.card.dataStore.CardDataStoreFixture;
import org.softwareFm.swt.card.internal.Card;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.details.internal.DetailFactory;
import org.softwareFm.swt.swt.SwtTest;
import org.softwareFm.swt.title.TitleSpec;

abstract public class AbstractDetailTest extends SwtTest {
	protected static LineItem stringValue = new LineItem(null, "key", "stringValue");
	protected static LineItem intValue = new LineItem(null, "key", 0);
	protected static LineItem mapValue = new LineItem(null, "key", Maps.stringObjectMap("k1", "v1", "k2", "v2"));
	protected static LineItem collectionValue = new LineItem(null, "key", Maps.stringObjectMap("k1", "v1", "k2", "v2", "sling:resourceType", "collection"));
	protected static LineItem typedValueNotCollection = new LineItem(null, "key", Maps.stringObjectMap("k1", "v1", "k2", "v2", "sling:resourceType", "sometype"));
	protected static LineItem folderValue = new LineItem(null, "key", Maps.stringObjectMap("k1", "v1", "k2", "v2"));

	protected CardConfig parentCardConfig;
	protected CardConfig cardConfig;
	protected Card parentCard;
	protected CardDataStoreAsyncMock cardDataStore;
	private IFunction1WithDispose<String, Image> imageFn;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardDataStore = CardDataStoreFixture.rawAsyncCardStore();
		parentCardConfig = makeCardConfig();
		imageFn = BasicImageRegisterConfigurator.imageFnForTests(shell);
		cardConfig = makeCardConfig().//
				withImageFn(imageFn).//
				withTitleSpecFn(Functions.<ICardData, TitleSpec> constant(TitleSpec.noTitleSpec(shell.getBackground())));
		parentCard = new Card(shell, cardConfig, "parentCardUrl", Collections.<String, Object> emptyMap());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cardConfig.dispose();
		imageFn.dispose();
	}

	protected CardConfig makeCardConfig() {
		return new CardConfig(ICardFactory.Utils.cardFactory(), cardDataStore);
	}

	protected void checkGetNull(DetailFactory detailFactory, LineItem lineItem) {
		IHasControl actual = detailFactory.makeDetail(shell, parentCard, cardConfig, lineItem.key, lineItem.value, IDetailsFactoryCallback.Utils.noCallback());
		assertNull(actual);
	}

}
package org.softwareFm.card.card.internal.details;

import java.util.Collections;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.card.internal.Card;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreAsyncMock;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.details.internal.DetailFactory;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.maps.Maps;

abstract public class AbstractDetailTest extends SwtIntegrationTest {
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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardDataStore = CardDataStoreFixture.rawAsyncCardStore();
		parentCardConfig = makeCardConfig();
		cardConfig = makeCardConfig().withTitleSpecFn(Functions.<ICard,TitleSpec>constant(TitleSpec.noTitleSpec(shell.getBackground())));
		parentCard = new Card(shell, cardConfig, "parentCardUrl", Collections.<String, Object> emptyMap());
	}

	protected CardConfig makeCardConfig() {
		return new CardConfig(ICardFactory.Utils.cardFactory(), cardDataStore);
	}

	protected void checkGetNull(DetailFactory detailFactory, LineItem lineItem) {
		IHasControl actual = detailFactory.makeDetail(shell, parentCard, cardConfig, lineItem.key, lineItem.value, IDetailsFactoryCallback.Utils.noCallback());
		assertNull(actual);
	}

}

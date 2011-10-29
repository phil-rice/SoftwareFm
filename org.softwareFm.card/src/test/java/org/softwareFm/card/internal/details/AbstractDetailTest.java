package org.softwareFm.card.internal.details;

import java.util.Arrays;
import java.util.Collections;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreAsyncMock;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.internal.Card;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.runnable.Runnables;

abstract public class AbstractDetailTest extends SwtIntegrationTest {
	protected static KeyValue stringValue = new KeyValue("key", "stringValue");
	protected static KeyValue intValue = new KeyValue("key", 0);
	protected static KeyValue listValue = new KeyValue("key", Arrays.asList(new KeyValue("k1", "v1"), new KeyValue("k2", "v2")));
	protected static KeyValue collectionValue = new KeyValue("key", Arrays.asList(new KeyValue("k1", "v1"), new KeyValue("k2", "v2"), new KeyValue("sling:resourceType", "collection")));
	protected static KeyValue folderValue = new KeyValue("key", Arrays.asList(new KeyValue("k1", "v1"), new KeyValue("k2", "v2"), new KeyValue("sling:resourceType", "folder")));

	protected CardConfig parentCardConfig;
	protected CardConfig cardConfig;
	protected Card parentCard;
	protected CardDataStoreAsyncMock cardDataStore;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardDataStore = CardDataStoreFixture.rawAsyncCardStore();
		parentCardConfig = new CardConfig(ICardFactory.Utils.cardFactory(), cardDataStore);
		cardConfig = new CardConfig(ICardFactory.Utils.cardFactory(), cardDataStore);
		parentCard = new Card(shell, cardConfig, "parentCardUrl", Collections.<String, Object> emptyMap());
	}

	protected void checkGetNull(DetailFactory detailFactory, KeyValue keyValue) {
		IHasControl actual = detailFactory.makeDetail(shell, parentCard, cardConfig, keyValue, ICardSelectedListener.Utils.noListener(), Runnables.noRunnable);
		assertNull(actual);
	}

}

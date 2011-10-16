package org.softwareFm.card.internal;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.internal.Loading.LoadingComposite;
import org.softwareFm.card.internal.NameValue.NameValueComposite;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.future.GatedMockFuture;

public class CardFactoryIntegrationTest extends SwtIntegrationTest {


	public void testDefaultTitleBasedOnUrl() {
		CardFactory factory = new CardFactory();
		Card card = (Card) factory.makeCard(shell, CardDataStoreFixture.rawAsyncCardStore(), CardDataStoreFixture.url1a);
		assertEquals("1a", card.content.getText());
	}

	@SuppressWarnings("unused")
	public void testDefaultWithJustAttributeValuePairsBeforeFutureFinishes() {
		CardFactory factory = new CardFactory();
		Card card = (Card) factory.makeCard(shell, CardDataStoreFixture.rawAsyncCardStore(), CardDataStoreFixture.url1a);
		Control[] children = card.content.getChildren();
		assertEquals(1, children.length);
		LoadingComposite loading = (LoadingComposite) children[0];
	}

	public void testDefaultWithJustAttributeValuePairsAfterFutureFinishes() {
		CardFactory factory = new CardFactory();
		Card card = (Card) factory.makeCard(shell, CardDataStoreFixture.rawAsyncCardStore(), CardDataStoreFixture.url1a);
		GatedMockFuture<?> future = (GatedMockFuture<?>) card.future;
		future.kick();
		dispatchUntilQueueEmpty();

		Control[] children = card.content.getChildren();
		assertEquals(2, children.length);
		NameValueComposite line0 = (NameValueComposite) children[0];
		NameValueComposite line1 = (NameValueComposite) children[1];
		assertEquals("tag", line0.name);
		assertEquals("one", line0.value);
		assertEquals("value", line1.name);
		assertEquals("valuea", line1.value);
	}
	
	
}

package org.softwareFm.card.internal;

import org.junit.Test;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreAsyncMock;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.internal.CardExplorer.CardExplorerComposite;
import org.softwareFm.card.navigation.NavBar;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.future.GatedMockFuture;

public class CardExplorerTest extends SwtIntegrationTest {

	private CardExplorer cardExplorer;
	private CardHolder cardHolder;
	private CardDataStoreAsyncMock cardDataStore;
	private CardExplorerComposite cardExplorerComposite;

	@Test
	public void testConstructor() {
		assertTrue(cardExplorer.getControl() instanceof CardExplorer.CardExplorerComposite);
		assertSame(cardExplorer.getControl(), cardExplorer.getComposite());
	}

	public void testConstructorCreatesANavBarInTheCardHolder() {
		NavBar navBar = (NavBar) cardHolder.content.title;
		assertNotNull(navBar);
		assertEquals(CardDataStoreFixture.url, navBar.getRootUrl());
	}

	public void testConstructorDoesntPopulateTheNavBarUntil() {
		NavBar navBar = (NavBar) cardHolder.content.title;
		assertNull(navBar.getCurrentUrl());
	}

	public void testConstructorDoesntPopulateTheCardHolder() {
		assertNull(cardHolder.content.card);
	}

	public void testConstructorSentInitialUrlToCardDataStore() {
		assertEquals(1, cardDataStore.counts.get(CardDataStoreFixture.url1a).intValue());
		assertNotNull(cardExplorerComposite.cardFuture);
	}

	public void testConstructorCausedNavBarToSentRootUrlToCardDataStore() {// later we may change this and have navbar showing url immediately
		assertEquals(null, cardDataStore.counts.get(CardDataStoreFixture.url));
	}

	public void testWhenCardDataStoreRespondsCardValueIsChanged() {
		causeCardDataStoreToRespond();
		ICard card = cardHolder.content.card;
		assertEquals(CardDataStoreFixture.url1a, card.url());
		assertEquals(CardDataStoreFixture.data1ap, card.rawData());
	}

	private void causeCardDataStoreToRespond() {
		((GatedMockFuture<?,?>) cardExplorerComposite.cardFuture).kick();
		dispatchUntilQueueEmpty();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardDataStore = CardDataStoreFixture.rawAsyncCardStore();
		cardExplorer = new CardExplorer(shell, new CardConfig(ICardFactory.Utils.cardFactory(), cardDataStore), CardDataStoreFixture.url);
		cardExplorerComposite = (CardExplorer.CardExplorerComposite) cardExplorer.getComposite();
		cardHolder = cardExplorerComposite.left;
		
	}

}

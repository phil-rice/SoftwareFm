package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.swt.SwtIntegrationTest;

public class MultipleCardsTest extends SwtIntegrationTest {

	private MultipleCards multipleCards;

	public void testCardsGetAdded() {
		ICard card0 = multipleCards.openCardAsChildOf(null, CardDataStoreFixture.url);
		ICard card1 = multipleCards.openCardAsChildOf(null,  CardDataStoreFixture.url1a);
		dispatchUntilQueueEmpty();
		Composite composite = (Composite) multipleCards.getControl();
		Control[] children = composite.getChildren();
		assertEquals(2, children.length);
		assertEquals(card0.getControl(), children[0]);
		assertEquals(card1.getControl(), children[1]);
	}
	
	public void testAssumptionsAboutCards(){
		ICard card0 = multipleCards.openCardAsChildOf(null, CardDataStoreFixture.url);
		ICard card1 = multipleCards.openCardAsChildOf(null, CardDataStoreFixture.url1a);
		dispatchUntilQueueEmpty();
		assertEquals(new Point(567,189), card0.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT));
		assertEquals(new Point(162, 54), card1.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
	}
	
	public void testComputeSize(){
		ICard card0 = multipleCards.openCardAsChildOf(null, CardDataStoreFixture.url);
		ICard card1 = multipleCards.openCardAsChildOf(null, "1a");
		dispatchUntilQueueEmpty();
		
		checkComputedSize(SWT.DEFAULT, SWT.DEFAULT, 189*3*2, 189);
		
		checkComputedSize(SWT.DEFAULT, 200, 200*3*2, 200);
		checkComputedSize(SWT.DEFAULT, 189, 189*3*2, 189);
		checkComputedSize(SWT.DEFAULT, 50, 50*3*2, 50);
		
		checkComputedSize(10000, 50, 50*3*2, 50);
		checkComputedSize(1, 50, 50*3*2, 50);
		checkComputedSize(-50, 50, 50*3*2, 50);
		
	}

	private void checkComputedSize(int wHint, int hHint, int expectedWidth, int expectedHeight) {
		Control control = multipleCards.getControl();
		Point computedSize = control.computeSize(wHint, hHint);
		assertEquals(expectedWidth, computedSize.x);
		assertEquals(expectedHeight, computedSize.y);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
		multipleCards = new MultipleCards(shell, CardDataStoreFixture.rawCardStore(), cardFactory);
		CardConfig config = cardFactory.getCardConfig();
		config.cardHeightWeigth = 1;
		config.cardWidthWeight = 3;

	}
}

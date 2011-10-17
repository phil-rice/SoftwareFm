package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.swt.SwtIntegrationTest;

public class CardTest extends SwtIntegrationTest {

	private ICardFactory cardFactory;
	private CardConfig cardConfig;

	public void testCardGetsNameValueChildren() {
		ICard card = cardFactory.makeCard(shell, CardDataStoreFixture.rawCardStore(), CardDataStoreFixture.url);
		dispatchUntilQueueEmpty();
		Control[] children = card.getComposite().getChildren();
		assertEquals(7, children.length);
		assertEquals("data1a", ((NameValue.NameValueComposite) children[0]).name);
		assertEquals("data1b", ((NameValue.NameValueComposite) children[1]).name);
		assertEquals("data2a", ((NameValue.NameValueComposite) children[2]).name);
		assertEquals("data2b", ((NameValue.NameValueComposite) children[3]).name);
		assertEquals("data2c", ((NameValue.NameValueComposite) children[4]).name);
		assertEquals("name1", ((NameValue.NameValueComposite) children[5]).name);
		assertEquals("name2", ((NameValue.NameValueComposite) children[6]).name);
	}

	public void testComputeSize() {
		int maxHeight = 7 * (cardConfig.lineHeight + cardConfig.lineToLineGap);
		checkComputeSize(maxHeight * 2, maxHeight, SWT.DEFAULT, SWT.DEFAULT);
		checkComputeSize(80, 40, SWT.DEFAULT, 40);
		checkComputeSize(40, 20, 40, SWT.DEFAULT);
	}
	
	public void testComputeSizeWhenSpecifyBoth(){
		checkComputeSize(40, 20, 40, 20);//already ok
		checkComputeSize(40, 20, 40, 40);
		checkComputeSize(40, 20, 2000, 20); //very wide
		checkComputeSize(40, 20, 40, 2000); //very tall
	}

	private void checkComputeSize(int expectedWidth, int expectedHeight, int wHint, int hHint) {
		ICard card = cardFactory.makeCard(shell, CardDataStoreFixture.rawCardStore(), CardDataStoreFixture.url);
		dispatchUntilQueueEmpty();
		Control control = card.getControl();
		Point size = control.computeSize(wHint, hHint);
		assertEquals(expectedWidth, size.x);
		assertEquals(expectedHeight, size.y);
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardFactory = ICardFactory.Utils.cardFactory();
		cardConfig = cardFactory.getCardConfig();
		cardConfig.cardHeightWeigth = 2;
		cardConfig.cardWidthWeight = 4;

	}
}

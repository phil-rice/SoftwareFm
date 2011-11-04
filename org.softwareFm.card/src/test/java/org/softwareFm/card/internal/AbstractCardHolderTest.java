package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.display.swt.SwtIntegrationTest;

public abstract class AbstractCardHolderTest extends SwtIntegrationTest {

	protected CardConfig cardConfig;
	protected CardHolder cardHolder;
	private Rectangle expectedClientArea;
	private int borderThickness;

	abstract protected CardHolder makeCardHolder(Composite parent, CardConfig cardConfig);

	public void testCardIsNullWhenConstructed() {
		assertNull(cardHolder.content.card);
	}

	public void testDisplaysTitleOrNavBarWhenConstructed() {
		cardHolder.getComposite().layout();
		// assertTrue(getTitleControl().isVisible()); sadly cannot test this in integration test as...well..it's not actually visible
		checkTitleLayout(cardConfig);
	}

	abstract public void testNavBarOrTitleChangesWhenCardAppears() throws Exception;

	public void testDisplaysCardWhenCardAppears() {
		ICard card = makeAndSetCard(cardConfig);
		assertSame(card, cardHolder.content.card);
	}

	public void testGetClientAreaIsParentClientAreaModifiedByCardConfigMargins() {
		checkClientArea(cardConfig);
		checkClientArea(cardConfig.withMargins(1, 2, 3, 4));

		makeAndSetCard(cardConfig);

		checkClientArea(cardConfig);
		checkClientArea(cardConfig.withMargins(1, 2, 3, 4));
	}

	private void checkClientArea(CardConfig cardConfig) {
		Rectangle clientArea = cardHolder.getComposite().getClientArea();
		assertEquals(expectedClientArea, clientArea);
	}

	public void testLayoutPutsCardInPositionSpecifiedByMargin() {
		setCardAndcheckCardLayout(cardConfig);
		setCardAndcheckCardLayout(cardConfig.withMargins(1, 2, 3, 4));
		setCardAndcheckCardLayout(cardConfig.withMargins(1, 2, 3, 4).withTitleHeight(10));
		setCardAndcheckCardLayout(cardConfig.withMargins(1, 2, 3, 4).withTitleHeight(20));
	}

	private void setCardAndcheckCardLayout(CardConfig cardConfig) {
		ICard card = cardConfig.cardFactory.makeCard(cardHolder, cardConfig, "someRootUrl/someUrl", CardDataStoreFixture.data1a);
		cardHolder.setCard(card);
		checkCardLayout(cardConfig);
	}

	private void checkCardLayout(CardConfig cardConfig) {
		cardHolder.getComposite().layout();
		Rectangle bounds = cardHolder.content.card.getComposite().getBounds();
		assertEquals(cardConfig.leftMargin, bounds.x);
		assertEquals(cardConfig.topMargin + cardConfig.titleHeight, bounds.y);
		assertEquals(110 - cardConfig.leftMargin - cardConfig.rightMargin - borderThickness * 2, bounds.width);
		assertEquals(220 - cardConfig.topMargin - cardConfig.bottomMargin - cardConfig.titleHeight - borderThickness * 2, bounds.height);
	}

	public void testLayoutPutsTitleIntoPositionSpecifiedByCardConfigAfterSetCard() {
		setCardAndcheckTitleLayout(cardConfig);
		setCardAndcheckTitleLayout(cardConfig.withMargins(1, 2, 3, 4));
		setCardAndcheckTitleLayout(cardConfig.withMargins(1, 2, 3, 4).withTitleHeight(10));
		setCardAndcheckTitleLayout(cardConfig.withMargins(1, 2, 3, 4).withTitleHeight(20));

	}

	private void setCardAndcheckTitleLayout(CardConfig cardConfig) {
		makeAndSetCard(cardConfig);
		checkTitleLayout(cardConfig);
	}

	private void checkTitleLayout(CardConfig cardConfig) {
		cardHolder.getComposite().layout();

		Rectangle bounds = getTitleControl().getBounds();
		assertEquals(cardConfig.leftMargin, bounds.x);
		assertEquals(cardConfig.topMargin, bounds.y);
		assertEquals(110 - cardConfig.leftMargin - cardConfig.rightMargin - borderThickness * 2, bounds.width);
		assertEquals(cardConfig.titleHeight, bounds.height);
	}

	public void testCardLaysItSelfOutWhenResized() {

	}

	protected ICard makeAndSetCard(CardConfig cardConfig) {
		ICard card = cardConfig.cardFactory.makeCard(cardHolder, cardConfig, "someRootUrl/someUrl", CardDataStoreFixture.data1a);
		cardHolder.setCard(card);
		return card;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(shell.getDisplay());
		Composite parent = new Composite(shell, SWT.NULL) {
			@Override
			public Rectangle getClientArea() {
				return new Rectangle(10, 20, 110, 220);
			}
		};
		cardHolder = makeCardHolder(parent, cardConfig);
		borderThickness = 2;
		expectedClientArea = new Rectangle(cardConfig.leftMargin, //
				cardConfig.topMargin,//
				110 - cardConfig.leftMargin - cardConfig.rightMargin - borderThickness * 2,//
				220 - cardConfig.topMargin - cardConfig.bottomMargin - borderThickness * 2);
		cardHolder.getControl().setBounds(10, 20, 110, 220);
	}

	protected Control getTitleControl() {
		return cardHolder.content.title.getControl();
	}

}

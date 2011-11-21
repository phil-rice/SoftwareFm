package org.softwareFm.card.card.internal;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.card.card.CardChangedListenerMock;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public abstract class AbstractCardHolderTest extends SwtIntegrationTest {

	protected CardConfig cardConfig;
	protected CardHolder cardHolder;
	private Rectangle expectedClientArea;
	private int borderThickness;
	protected final String rootUrl = "some/rootUrl";

	private CardChangedListenerMock mock1;
	private CardChangedListenerMock mock2;

	abstract protected CardHolder makeCardHolder(Composite parent, CardConfig cardConfig);

	public void testCardIsNullWhenConstructed() {
		assertNull(cardHolder.content.card);
	}

	public void testCompositeIsCardHoldComposite() {
		assertTrue(cardHolder.content instanceof CardHolder.CardHolderComposite);
		assertSame(cardHolder.content, cardHolder.getControl());
		assertSame(cardHolder.content, cardHolder.getComposite());
	}

	public void testCardListenersInformedWhenSetCardCalled() {
		ICard newCard = makeAndSetCard(cardConfig);
		checkCardChangedListener(mock1, newCard);
		checkCardChangedListener(mock2, newCard);

	}

	private void checkCardChangedListener(CardChangedListenerMock mock, ICard newCard) {
		assertEquals(newCard, Lists.getOnly(mock.cardChangedCards));
		assertEquals(cardHolder, Lists.getOnly(mock.cardHolders));
	}

	public void testCardChangedListenersNotifiedWhenValuesUpdated() {
		ICard newCard = makeAndSetCard(cardConfig);
		checkValueChanged(mock1, newCard);
		checkValueChanged(mock2, newCard);

		newCard.valueChanged("value", "newValue");// the key is "value", and the newValue is "newValue"
		checkValueChanged(mock1, newCard, "value", "newValue");
		checkValueChanged(mock2, newCard, "value", "newValue");
	}

	public void testMenuListenerInvokedWhenTableRightClicked(){
		ICard card = makeAndSetCard(cardConfig);
		final List<Event> events = Lists.newList();
		card.addMenuDetectListener(new Listener() {
			@Override
			public void handleEvent(Event event) {events.add(event);
			}
		});
		Table table = card.getTable();
		
		Point control = table.toDisplay(new Point(2,2));//just inside the first item: "tag"
		Event event = new Event();
		event.x = control.x;
		event.y = control.y;
		
		table.notifyListeners(SWT.MenuDetect, event);
		dispatchUntilQueueEmpty();
		assertEquals(event, Lists.getOnly(events));
	}

	private void checkValueChanged(CardChangedListenerMock mock, ICard card, Object... keyAndValues) {
		Map<Object, Object> map = Maps.makeLinkedMap(keyAndValues);
		assertEquals(Iterables.list(map.keySet()), mock.keys);
		assertEquals(Iterables.list(map.values()), mock.newValues);
		assertEquals(Lists.times(card, map.size()), mock.valueChangedCards);
	}

	public void testClientAreaIsBounds() {
		checkClientAreaIsBounds();
	}

	private void checkClientAreaIsBounds() {
		Rectangle bounds = cardHolder.getComposite().getBounds();
		Rectangle expected = new Rectangle(0, 0, bounds.width, bounds.height);
		assertEquals(expected, cardHolder.getComposite().getClientArea());
	}

	public void testDisplaysCardWhenCardAppears() {
		ICard card = makeAndSetCard(cardConfig);
		assertSame(card, cardHolder.content.card);
	}

	protected ICard makeAndSetCard(CardConfig cardConfig) {
		ICard card = cardConfig.cardFactory.makeCard(cardHolder, cardConfig, rootUrl + "/someUrl", CardDataStoreFixture.data1a);
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
		borderThickness = 0;
		expectedClientArea = new Rectangle(cardConfig.leftMargin, //
				cardConfig.topMargin,//
				110 - cardConfig.leftMargin - cardConfig.rightMargin - borderThickness * 2,//
				220 - cardConfig.topMargin - cardConfig.bottomMargin - borderThickness * 2);
		cardHolder.getControl().setBounds(10, 20, 110, 220);
		mock1 = new CardChangedListenerMock();
		mock2 = new CardChangedListenerMock();
		cardHolder.addCardChangedListener(mock1);
		cardHolder.addCardChangedListener(mock2);
	}

	protected Control getTitleControl() {
		return cardHolder.content.title.getControl();
	}

}

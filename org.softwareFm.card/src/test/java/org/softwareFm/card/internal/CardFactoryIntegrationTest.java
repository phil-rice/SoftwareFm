package org.softwareFm.card.internal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.card.api.ILine;
import org.softwareFm.card.internal.NameValue.NameValueComposite;
import org.softwareFm.card.internal.TextLine.LoadingComposite;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.callbacks.ICallback;
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

	public void testAddLineSelectedListenerWhenClickedOnName() throws Exception {
		checkListenersCalled(new ICallback<NameValue>() {
			@Override
			public void process(NameValue t) throws Exception {
				t.content.lblName.notifyListeners(SWT.MouseDown, new Event());
			}
		});
	}

	public void testAddLineSelectedListenerWhenClickedOnValue() throws Exception {
		checkListenersCalled(new ICallback<NameValue>() {
			@Override
			public void process(NameValue t) throws Exception {
				t.content.txtValue.notifyListeners(SWT.MouseDown, new Event());
			}
		});
	}

	public void testAddLineSelectedListenerWhenClickedOnBackground() throws Exception {
		checkListenersCalled(new ICallback<NameValue>() {
			@Override
			public void process(NameValue t) throws Exception {
				t.content.notifyListeners(SWT.MouseDown, new Event());
			}
		});
	}

	private void checkListenersCalled(ICallback<NameValue> callback) throws Exception {
		List<String> expected = Arrays.asList("tag", "value");
		CardFactory factory = new CardFactory();
		Card card = (Card) factory.makeCard(shell, CardDataStoreFixture.rawCardStore(), CardDataStoreFixture.url1a);
		dispatchUntilQueueEmpty();

		LineSelectedListenerMock mock1 = new LineSelectedListenerMock();
		LineSelectedListenerMock mock2 = new LineSelectedListenerMock();
		card.addLineSelectedListener(mock1);
		card.addLineSelectedListener(mock2);
		for (ILine line : card.content.lines) {
			NameValue nameValue = (NameValue) line;
			callback.process(nameValue);
		}
		dispatchUntilQueueEmpty();

		assertEquals(expected, mock1.names);
		assertEquals(expected, mock2.names);
	}

}

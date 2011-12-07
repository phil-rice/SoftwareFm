package org.softwareFm.collections.explorer.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.collections.explorer.internal.AddCard.AddCardComposite;
import org.softwareFm.display.swt.SwtTest;

public class AddCardTest extends SwtTest {

	private CardConfig cardConfig;
	private final String url = "someUrl";
	private final String title = "someTitle";
	private final String cardType = "someCardType";
	private AddCardCallbackMock callback;

	public void testGetCompositeAndGetControlAreSame() {
		AddCard addCard = makeCard();
		AddCardComposite composite = (AddCardComposite) addCard.getComposite();
		assertSame(composite, addCard.getControl());
		assertSame(composite, addCard.content);
	}

	public void testMakesThreeChildrenTitleCardAndOkCancel() {// but cannot easily verify that
		AddCard card = makeCard();
		AddCardComposite composite = card.content;
		Control[] children = composite.getChildren();
		assertEquals(3, children.length);
		assertEquals(composite.title.getControl(), children[0]);
		assertEquals(composite.card.getControl(), children[1]);
		assertEquals(composite.okCancel.getControl(), children[2]);
	}
	
	
	public void testEditorIsCreatedIfYouSelectLineOnTable(){
		AddCard card = makeCard();
		assertNull(card.content.editor.getEditor());//start with no editor
		checkEditorInitialValue(card, "tag", "one");
		checkEditorInitialValue(card, "value", "valuea");
		checkEditorInitialValue(card, "p", "1");
	}
	
	public void testEditingUpdatesBackingData(){
		AddCard card = makeCard();
		Text text1 = checkEditorInitialValue(card, "tag", "one");
		text1.setText("two"); //calls modify listener
		assertEquals("two", card.data().get("tag"));
		
		Text text2 = checkEditorInitialValue(card, "value", "valuea");
		text2.setText("valueb"); //calls modify listener
		assertEquals("two", card.content.card.data().get("tag"));
		assertEquals("valueb", card.data().get("value"));

		Text text3 = checkEditorInitialValue(card, "tag", "two");
		text3.setText("three"); //calls modify listener
		assertEquals("three", card.data().get("tag"));
		assertEquals("valueb", card.data().get("value"));
	}

	private Text checkEditorInitialValue(AddCard card, String key, String value) {
		TableEditor editor = card.content.editor;
		Table table = card.content.table;
		select(table, 0, key);
		table.notifyListeners(SWT.Selection, new Event());
		Text itemEditor = (Text) editor.getEditor();
		assertEquals(value, itemEditor.getText());
		return itemEditor;
	}

	private AddCard makeCard() {
		AddCard addCard = new AddCard(shell, cardConfig, url, title, cardType, CardDataStoreFixture.data1aWithP1Q2, callback);
		return addCard;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(display);
		callback = new AddCardCallbackMock();
	};
}

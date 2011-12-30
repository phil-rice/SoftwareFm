package org.softwareFm.card.editors.internal;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.editors.AddCardCallbackMock;
import org.softwareFm.card.editors.IValueEditor;
import org.softwareFm.card.editors.internal.SnippetEditor.SnippetEditorComposite;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class SnippetEditorTest extends SwtTest {

	private CardConfig cardConfig;
	String title = "someTitle";
	String description = "someDescription";
	String content = "someContent";

	public void testPopulatesFieldsWithData() {
		Map<String, Object> data = Maps.stringObjectMap("title", title, "content", content, "description", description);
		SnippetEditor editor = (SnippetEditor) IValueEditor.Utils.snippetEditorWithLayout(shell, cardConfig, "Title", "someUrl", data, new AddCardCallbackMock());
		SnippetEditorComposite composite = (SnippetEditorComposite) editor.getComposite();
		assertEquals(title, composite.titleText.getText());
		assertEquals(description, composite.descriptionText.getText());
		assertEquals(content, composite.contentText.getText());
	}

	public void testUpdatingFieldsChangesCardData() {
		Map<String, Object> data = Maps.stringObjectMap("title", title, "content", content, "description", description);
		SnippetEditor editor = (SnippetEditor) IValueEditor.Utils.snippetEditorWithLayout(shell, cardConfig, "Title", "someUrl", data, new AddCardCallbackMock());
		SnippetEditorComposite composite = (SnippetEditorComposite) editor.getComposite();
		checkChange(editor.data(), composite.titleText, "title");
		checkChange(editor.data(), composite.descriptionText, "description");
		checkChange(editor.data(), composite.contentText, "content");
	}

	
	public void testOkUsesUptodateData(){
		AddCardCallbackMock mock = new AddCardCallbackMock();

		Map<String, Object> data = Maps.stringObjectMap("title", title, "content", content, "description", description);
		SnippetEditor editor = (SnippetEditor) IValueEditor.Utils.snippetEditorWithLayout(shell, cardConfig, "Title", "someUrl", data, mock);
		SnippetEditorComposite composite = (SnippetEditorComposite) editor.getComposite();
		checkChange(editor.data(), composite.titleText, "title");
		checkChange(editor.data(), composite.descriptionText, "description");
		checkChange(editor.data(), composite.contentText, "content");

		mock.setCanOk(true);
		composite.getOkCancel().ok();
		assertEquals(Maps.stringObjectMap("title", "someNewtitle", "content", "someNewcontent", "description", "someNewdescription", CardConstants.slingResourceType, CardConstants.snippet), Lists.getOnly(mock.okData));
		
		
	}
	
	private void checkChange(Map<String, Object> data, Text text, String key) {
		text.setText("someNew" + key);
		text.notifyListeners(SWT.Modify, new Event());
		assertEquals("someNew" + key, data.get(key));
	}

	private void checkChange(Map<String, Object> data, StyledText text, String key) {
		text.setText("someNew" + key);
		text.notifyListeners(SWT.Modify, new Event());
		assertEquals("someNew" + key, data.get(key));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(display);
	}

}

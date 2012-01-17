package org.softwareFm.card.editors.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.editors.AddCardCallbackMock;
import org.softwareFm.card.editors.INamesAndValuesEditor;
import org.softwareFm.card.editors.IValueComposite;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class NameAndValuesEditorTest extends SwtTest {

	private CardConfig cardConfig;
	private final String cardType = "someCardType";
	private AddCardCallbackMock callback;
	private final Map<String, Object> initialData = Maps.stringObjectMap("one", "1", "two", "2");
	private Composite labels;
	private Composite values;
	private OkCancel okCancel;
	private int changeValueIndex;
	private NameAndValuesEditor editor;

	public void testInitialValuesWhenDisplayed() {
		checkLabelsMatch(labels, "one", "two", "three");
		checkValuesMatch(values, "1", "2", "");
		assertFalse(okCancel.isOkEnabled());
	}

	public void testEditorUpdatesOKEnabledAndCardData() {
		checkChangeValue(0, "newOne", false);
		checkChangeValue(1, "newTwo", false);
		checkChangeValue(2, "newThree", false);
		callback.setCanOk(true);
		assertFalse(okCancel.isOkEnabled());
		checkChangeValue(1, "newerTwo", true);
		assertEquals(0, callback.okData.size());
		
		okCancel.okButton.notifyListeners(SWT.Selection, new Event());
		assertEquals(Maps.stringObjectMap("one", "newOne", "two", "newerTwo", "three", "newThree", CardConstants.slingResourceType, cardType), Lists.getOnly(callback.okData));
	}

	private void checkChangeValue(int index, String newValue, boolean expectedOkEnabled) {
		Label label = (Label) labels.getChildren()[index];
		String key = label.getText();
		Control control = values.getChildren()[index];
		assertEquals(changeValueIndex + 1, callback.canOkData.size());
		Swts.setText(control, newValue);
		changeValueIndex++;
		assertEquals(changeValueIndex + 1, callback.canOkData.size());
		String value = (String) editor.data().get(key);
		Map<String, Object> actual = callback.canOkData.get(changeValueIndex);
		assertEquals(newValue, value);
		assertEquals(newValue, actual.get(key));
		assertEquals(expectedOkEnabled, okCancel.isOkEnabled());

	}

	private void checkValuesMatch(Composite values, String... expected) {
		Control[] children = values.getChildren();
		for (int i = 0; i < children.length; i++) {
			Control control = children[i];
			if (control instanceof Text)
				assertEquals(expected[i], ((Text) control).getText());
			else if (control instanceof StyledText)
				assertEquals(expected[i], ((StyledText) control).getText());
			else
				throw new IllegalArgumentException(control.getClass().getName());
		}
	}

	private void checkLabelsMatch(Composite labels, String... strings) {
		Control[] children = labels.getChildren();
		for (int i = 0; i < children.length; i++) {
			Label label = (Label) children[i];
			assertEquals(strings[i], label.getText());
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = CardDataStoreFixture.syncCardConfig(display);
		callback = new AddCardCallbackMock();
		editor = new NameAndValuesEditor(shell, cardConfig, cardType, "someTitle", "someurl", initialData, Arrays.asList(//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, "one"),//
				INamesAndValuesEditor.Utils.styledText(cardConfig, cardType, "two"),//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, "three")), callback);
		IValueComposite<SashForm> composite = (IValueComposite<SashForm>) editor.getComposite();
		okCancel = composite.getOkCancel();
		SashForm sashForm = composite.getEditor();
		assertEquals(2, sashForm.getChildren().length);
		labels = (Composite) sashForm.getChildren()[0];
		values = (Composite) sashForm.getChildren()[1];
	}

}

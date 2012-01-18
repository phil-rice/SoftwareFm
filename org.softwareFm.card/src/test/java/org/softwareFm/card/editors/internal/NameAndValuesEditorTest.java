package org.softwareFm.card.editors.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.editors.AbstractNameAndValuesEditorTest;
import org.softwareFm.card.editors.INamesAndValuesEditor;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class NameAndValuesEditorTest extends AbstractNameAndValuesEditorTest<NameAndValuesEditor> {

	private final Map<String, Object> initialData = Maps.stringObjectMap("one", "1", "two", "2");
	private int changeValueIndex;

	public void testInitialValuesWhenDisplayed() {
		checkLabelsMatch(labels, "one", "two", "three");
		checkTextMatches(values, "1", "2", "");
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


	@Override
	protected String getCardType() {
		return "someCardType";
	}
	@Override
	protected NameAndValuesEditor makeEditor() {
		return new NameAndValuesEditor(shell, cardConfig, cardType, "someTitle", "someurl", initialData, Arrays.asList(//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, "one"),//
				INamesAndValuesEditor.Utils.styledText(cardConfig, cardType, "two"),//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, "three")), callback);	}

}

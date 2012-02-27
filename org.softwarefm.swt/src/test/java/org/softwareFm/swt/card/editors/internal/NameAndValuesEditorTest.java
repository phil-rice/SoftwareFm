/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.editors.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.card.editors.AbstractNameAndValuesEditorTest;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.editors.NameAndValuesEditor;
import org.softwareFm.swt.swt.Swts;

public class NameAndValuesEditorTest extends AbstractNameAndValuesEditorTest<NameAndValuesEditor> {

	private final Map<String, Object> initialData = Maps.stringObjectMap("one", "1", "two", "2");
	private int changeValueIndex;

	public void testInitialValuesWhenDisplayed() {
		checkLabelsMatch(labels, "One", "Two", "Three");
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
		Swts.Buttons.press(okCancel.okButton());
		assertEquals(Maps.stringObjectMap("one", "newOne", "two", "newerTwo", "three", "newThree", CardConstants.slingResourceType, cardType), Lists.getOnly(callback.okData));
	}

	private void checkChangeValue(int index, String newValue, boolean expectedOkEnabled) {
		Label label = (Label) labels.getChildren()[index];
		String key = label.getText().toLowerCase();// won't work in general, but is ok for one/two/three
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

	public void testSelectCallsOKIfOkEnabled() {
		Text text = (Text) values.getChildren()[0];

		assertEquals(0, callback.okData.size());
		text.notifyListeners(SWT.DefaultSelection, new Event());
		assertEquals(0, callback.okData.size());

		callback.setCanOk(true);
		text.notifyListeners(SWT.Modify, new Event());
		text.notifyListeners(SWT.DefaultSelection, new Event());
		assertEquals(1, callback.okData.size());
	}

	@Override
	protected String getCardType() {
		return "someCardType";
	}

	@Override
	protected NameAndValuesEditor makeEditor() {
		return new NameAndValuesEditor(shell, cardConfig, cardType, "someTitle", "someurl", initialData, Arrays.asList(//
				INamesAndValuesEditor.Utils.text(cardConfig, "one"),//
				INamesAndValuesEditor.Utils.styledText(cardConfig, "two"),//
				INamesAndValuesEditor.Utils.text(cardConfig, "three")), callback);
	}

}
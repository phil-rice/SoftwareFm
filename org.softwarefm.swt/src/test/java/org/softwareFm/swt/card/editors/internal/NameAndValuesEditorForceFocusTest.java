/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.editors.internal;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.editors.AbstractNameAndValuesEditorTest;
import org.softwareFm.swt.editors.IEditableControlStrategy;
import org.softwareFm.swt.editors.KeyAndEditStrategy;
import org.softwareFm.swt.editors.NameAndValuesEditor;
import org.softwareFm.swt.okCancel.IOkCancel;

public class NameAndValuesEditorForceFocusTest extends AbstractNameAndValuesEditorTest<NameAndValuesEditor> {

	public void testForceFocus() {
		editor.getComposite().forceFocus();
		Control[] children = editorComposite.getChildren();
		for (int i = 0; i < children.length; i += 2) {
			assertTrue(children[i + 0] instanceof Label);
			ControlWithForceFocusIntercept control = (ControlWithForceFocusIntercept) children[i + 1];
			assertEquals("I: " + i, i == 2, control.hasBeenForced);
		}
	}

	@Override
	protected String getCardType() {
		return "someCardType";
	}

	@Override
	protected NameAndValuesEditor makeEditor() {
		NameAndValuesEditor nameAndValuesEditor = new NameAndValuesEditor(shell, cardConfig, cardType, "someTitle", "someurl", Maps.emptyStringObjectMap(), Arrays.asList(//
				new KeyAndEditStrategy("one", new ControlWithForceFocusEditableControlStrategy(false)),//
				new KeyAndEditStrategy("two", new ControlWithForceFocusEditableControlStrategy(true)),//
				new KeyAndEditStrategy("three", new ControlWithForceFocusEditableControlStrategy(false))), callback);
		return nameAndValuesEditor;
	}

	static class ControlWithForceFocusEditableControlStrategy implements IEditableControlStrategy<ControlWithForceFocusIntercept> {

		private final boolean canAccept;

		public ControlWithForceFocusEditableControlStrategy(boolean canAccept) {
			this.canAccept = canAccept;
		}

		@Override
		public ControlWithForceFocusIntercept createControl(Composite from) {
			return new ControlWithForceFocusIntercept(from, SWT.NULL);
		}

		@Override
		public void populateInitialValue(ControlWithForceFocusIntercept control, Object value) {
		}

		@Override
		public void whenModifed(ControlWithForceFocusIntercept control, ICardData cardData, String key, Runnable whenModified) {
		}

		@Override
		public void addEnterEscapeListeners(IOkCancel okCancel, ControlWithForceFocusIntercept control) {
		}

		@Override
		public boolean forceFocus(ControlWithForceFocusIntercept control) {
			if (canAccept)
				return control.forceFocus();
			return false;
		}

	}

	static class ControlWithForceFocusIntercept extends Text {

		public boolean hasBeenForced;

		public ControlWithForceFocusIntercept(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		protected void checkSubclass() {
		}

		@Override
		public boolean forceFocus() {
			hasBeenForced = true;
			return super.forceFocus();
		}

		@Override
		public String toString() {
			return "(force: " + hasBeenForced + ") " + super.toString();
		}

	}

}
package org.softwareFm.swt.card.editors.internal;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.editors.AbstractNameAndValuesEditorTest;
import org.softwareFm.swt.editors.IEditableControlStrategy;
import org.softwareFm.swt.editors.KeyAndEditStrategy;
import org.softwareFm.swt.editors.NameAndValuesEditor;
import org.softwareFm.swt.okCancel.IOkCancel;

public class NameAndValuesEditorForceFocusTest extends AbstractNameAndValuesEditorTest<NameAndValuesEditor> {

	public void testForceFocus() {
		editorComposite.forceFocus(); // sadly will return false in this test, as the composite is not actually displayed
		Control[] children = editorComposite.getChildren();
		for (int i = 0; i < children.length; i += 2) {
			assertTrue(children[i + 0] instanceof Label);
			ControlWithForceFocusIntercept control = (ControlWithForceFocusIntercept) children[i+1];
			assertEquals("I: "+ i, i==0, control.hasBeenForced);
		}
	}


	@Override
	protected String getCardType() {
		return "someCardType";
	}

	@Override
	protected NameAndValuesEditor makeEditor() {
		NameAndValuesEditor nameAndValuesEditor = new NameAndValuesEditor(shell, cardConfig, cardType, "someTitle", "someurl", Maps.emptyStringObjectMap(), Arrays.asList(//
				new KeyAndEditStrategy("one", new ControlWithForceFocusEditableControlStrategy()),//
				new KeyAndEditStrategy("two", new ControlWithForceFocusEditableControlStrategy()),//
				new KeyAndEditStrategy("three", new ControlWithForceFocusEditableControlStrategy())), callback);
		return nameAndValuesEditor;
	}

	static class ControlWithForceFocusEditableControlStrategy implements IEditableControlStrategy<ControlWithForceFocusIntercept> {

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

	}

}

package org.softwareFm.card.editors.internal;

import org.softwareFm.card.editors.IValueEditor;
import org.softwareFm.display.okCancel.OkCancel;

public interface IValueEditorForTests extends IValueEditor {
	String getTitleText();

	/** The text representation that would be stored if ok was to be pressed */
	String getValue();

	OkCancel getOkCancel();

	/** Set the value so that if getValue was called it would return this */
	void setValue(String newValue);

}

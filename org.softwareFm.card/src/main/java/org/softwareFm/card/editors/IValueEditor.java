package org.softwareFm.card.editors;

import org.softwareFm.display.composites.IHasComposite;

/** This interface mostly exists for tests */
public interface IValueEditor extends IHasComposite {

	String getTitleText();

	OkCancel getOkCancel();

	/**The text representation that would be stored if ok was to be pressed */
	String getValue();
	
	/**Set the value so that if getValue was called it would return this */
	void setValue(String newValue);
}

package org.softwareFm.card.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.title.Title;

public interface IValueComposite<T extends Control> {
	CardConfig getCardConfig();

	Title getTitle();

	Composite getBody();

	Composite getInnerBody();

	T getEditor();

	OkCancel getOkCancel();

	boolean useAllHeight();

}

package org.softwareFm.card.editors.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.internal.CardOutlinePaintListener;
import org.softwareFm.card.title.Title;
import org.softwareFm.display.okCancel.OkCancel;

/** represents the usual editor composite seen when editing lines. <T> is the type of the editor embedded in the composite */
public interface IValueComposite<T extends Control> {
	
	CardConfig getCardConfig();

	Title getTitle();

	/** The body is everything that isn't the title */
	Composite getBody();

	/** The inner body is needed to allow the {@link CardOutlinePaintListener} to draw around it. the editor and the ok cancel are children of it */
	Composite getInnerBody();

	/** the actual editor component */
	T getEditor();

	
	OkCancel getOkCancel();

	/** should the editor use all the available height, or it's own computed size */
	boolean useAllHeight();

}

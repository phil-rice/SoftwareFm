package org.softwareFm.swt.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.swt.card.CardOutlinePaintListener;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.okCancel.IOkCancel;
import org.softwareFm.swt.title.TitleSpec;
import org.softwareFm.swt.title.TitleWithTitlePaintListener;

public interface IDataComposite<T extends Control> {
	CardConfig getCardConfig();

	TitleWithTitlePaintListener getTitle();

	/** The body is everything that isn't the title */
	Composite getBody();

	/** The inner body is needed to allow the {@link CardOutlinePaintListener} to draw around it. the editor and and any footers (e.g. okCancel) are children of it */
	Composite getInnerBody();

	/** the actual editor component */
	T getEditor();

	/** should the editor use all the available height, or it's own computed size */
	boolean useAllHeight();

	TitleSpec getTitleSpec();

	IOkCancel getOkCancel();
}

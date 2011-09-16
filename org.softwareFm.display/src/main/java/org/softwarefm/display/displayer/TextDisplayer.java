package org.softwarefm.display.displayer;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.composites.TitleAndText;
import org.softwarefm.display.impl.DisplayerDefn;

public class TextDisplayer implements IDisplayerFactory {

	private final boolean mutable;

	public TextDisplayer(boolean mutable) {
		this.mutable = mutable;
	}

	@Override
	public IDisplayer create(Composite parent, final DisplayerDefn defn, int style, CompositeConfig compositeConfig) {
		TitleAndText titleAndText = new TitleAndText(compositeConfig, parent, defn.title, true);
		titleAndText.setGlobalEdit(mutable);
		return titleAndText;
	}

}

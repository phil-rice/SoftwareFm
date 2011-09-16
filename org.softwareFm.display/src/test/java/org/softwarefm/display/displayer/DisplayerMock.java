package org.softwarefm.display.displayer;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.impl.DisplayerDefn;

public class DisplayerMock implements IDisplayerFactory {

	private final String seed;

	public DisplayerMock(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "DisplayerMock [seed=" + seed + "]";
	}

	@Override
	public IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig) {
		throw new UnsupportedOperationException();
	}

}

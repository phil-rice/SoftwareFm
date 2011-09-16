package org.softwarefm.display.displayer;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.IControlWithToggle;
import org.softwarefm.display.impl.DisplayerDefn;

public class DisplayerMock implements IDisplayer {

	private final String seed;

	public DisplayerMock(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "DisplayerMock [seed=" + seed + "]";
	}

	@Override
	public IControlWithToggle create(Composite largeButtonComposite, DisplayerDefn defn, int style) {
		throw new UnsupportedOperationException();
	}

}

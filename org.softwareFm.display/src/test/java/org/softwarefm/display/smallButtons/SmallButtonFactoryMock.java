package org.softwarefm.display.smallButtons;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.IHasControl;
import org.softwarefm.display.impl.SmallButtonDefn;

public class SmallButtonFactoryMock implements ISmallButtonFactory {

	private final String seed;

	public SmallButtonFactoryMock(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "SmallButtonFactoryMock [seed=" + seed + "]";
	}

	@Override
	public IHasControl create(Composite parent, SmallButtonDefn smallButtonDefn, int style) {
		throw new UnsupportedOperationException();
	}

}

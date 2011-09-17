package org.softwarefm.display.smallButtons;

import org.softwareFm.swtBasics.IControlWithToggle;
import org.softwareFm.swtBasics.text.IButtonParent;
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
	public IControlWithToggle create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig imageButtonConfig) {
		throw new UnsupportedOperationException();
	}

}

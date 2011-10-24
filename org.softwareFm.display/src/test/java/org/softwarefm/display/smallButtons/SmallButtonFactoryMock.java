package org.softwareFm.display.smallButtons;

import org.softwareFm.display.displayer.ISmallDisplayer;
import org.softwareFm.display.simpleButtons.IButtonParent;

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
	public ISmallDisplayer create(IButtonParent parent, SmallButtonDefn smallButtonDefn, ImageButtonConfig imageButtonConfig) {
		throw new UnsupportedOperationException();
	}

}

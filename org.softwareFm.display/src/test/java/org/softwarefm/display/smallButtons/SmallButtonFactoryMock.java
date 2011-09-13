package org.softwarefm.display.smallButtons;

public class SmallButtonFactoryMock implements ISmallButtonFactory {

	private final String seed;

	public SmallButtonFactoryMock(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "SmallButtonFactoryMock [seed=" + seed + "]";
	}

}

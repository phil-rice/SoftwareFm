package org.softwarefm.display.displayer;

public class DisplayerMock implements IDisplayer {

	private final String seed;

	public DisplayerMock(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "DisplayerMock [seed=" + seed + "]";
	}

}

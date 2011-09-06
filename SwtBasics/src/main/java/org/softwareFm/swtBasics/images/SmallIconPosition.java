package org.softwareFm.swtBasics.images;

public enum SmallIconPosition {

	TopLeft(0, 0), //
	TopRight(8, 0), //
	BottomLeft(0, 8), //
	BottomRight(8, 8); //

	public final int x;
	public final int y;

	private SmallIconPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

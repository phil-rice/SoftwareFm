package org.softwareFm.swtBasics.images;

import java.util.Set;

import org.softwareFm.utilities.collections.Sets;

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

	public final static Set<SmallIconPosition> allIcons = Sets.makeSet(values());
	public final static Set<SmallIconPosition> allTop = Sets.makeSet(TopLeft, TopRight);
	public final static Set<SmallIconPosition> allBottom = Sets.makeSet(BottomLeft, BottomRight);
}

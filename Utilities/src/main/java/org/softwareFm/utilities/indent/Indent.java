package org.softwareFm.utilities.indent;

public class Indent {
	private final char fillChar;
	private final int depth;
	private final int perDepth;

	public Indent() {
		this(' ', 0, 2);
	}

	public Indent(char fillChar, int depth, int perDepth) {
		this.fillChar = fillChar;
		this.depth = depth;
		this.perDepth = perDepth;
	}

	public Indent indent() {
		return new Indent(fillChar, depth + 1, perDepth);
	}

	public String prefix() {
		int raw = depth * perDepth;
		int fill = raw < 0 ? 0 : raw;
		StringBuilder builder = new StringBuilder(fill);
		for (int i = 0; i < fill; i++)
			builder.append(fillChar);
		return builder.toString();
	}

	@Override
	public String toString() {
		return prefix();
	}
}

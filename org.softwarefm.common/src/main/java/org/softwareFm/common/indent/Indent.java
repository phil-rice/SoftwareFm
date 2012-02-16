/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.indent;

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

	public Indent unindent() {
		if (depth == 0)
			throw new IllegalStateException();
		return new Indent(fillChar, depth - 1, perDepth);
	}
}
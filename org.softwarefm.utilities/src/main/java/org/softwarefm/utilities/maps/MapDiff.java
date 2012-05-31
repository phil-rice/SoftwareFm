/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.maps;

import java.util.List;

import org.softwarefm.utilities.indent.Indent;

public class MapDiff<K> {
	public List<K> keysIn1Not2;
	public List<K> keysIn2Not1;
	public List<String> valuesDifferent;
	private final Indent indent;

	public MapDiff(List<K> keysIn1Not2, List<K> keysIn2Not1, List<String> valuesDifferent, Indent indent) {
		this.keysIn1Not2 = keysIn1Not2;
		this.keysIn2Not1 = keysIn2Not1;
		this.valuesDifferent = valuesDifferent;
		this.indent = indent;
	}

	@Override
	public String toString() {
		String prefix = indent.prefix();
		StringBuilder builder = new StringBuilder();
		builder.append(prefix + "MapDiff\n");
		builder.append(prefix + " keysIn1Not2=" + keysIn1Not2 + "\n");
		builder.append(prefix + " keysIn2Not1=" + keysIn2Not1 + "\n");
		builder.append(prefix + " valuesDifferent=\n");
		for (String diff : valuesDifferent)
			builder.append(prefix + "   " + diff + "\n");
		return builder.toString();
	}
}
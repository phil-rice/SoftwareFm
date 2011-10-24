package org.softwareFm.utilities.maps;

import java.util.List;

import org.softwareFm.utilities.indent.Indent;

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

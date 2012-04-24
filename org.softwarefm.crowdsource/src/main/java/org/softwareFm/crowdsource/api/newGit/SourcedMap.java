package org.softwareFm.crowdsource.api.newGit;

import java.util.Collections;
import java.util.Map;

public class SourcedMap {
	public final ISingleSource source;
	public final int index;
	public final Map<String, Object> map;

	public SourcedMap(ISingleSource source, int index, Map<String, Object> map) {
		this.source = source;
		this.index = index;
		this.map = Collections.unmodifiableMap(map);
	}

	@Override
	public String toString() {
		return "SourceAndData [source=" + source + ", index=" + index + ", map=" + map + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourcedMap other = (SourcedMap) obj;
		if (index != other.index)
			return false;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}
}

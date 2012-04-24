package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Map;

public class IndexAndMap {

	public final int index;
	public final Map<String, Object> map;

	public IndexAndMap(int index, Map<String, Object> map) {
		super();
		this.index = index;
		this.map = map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
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
		IndexAndMap other = (IndexAndMap) obj;
		if (index != other.index)
			return false;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IndexAndMap [index=" + index + ", map=" + map + "]";
	}

}

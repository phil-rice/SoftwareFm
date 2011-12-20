package org.softwareFm.server;

import java.util.Collections;
import java.util.Map;

public class GetResult {
	public final boolean found;
	public final Map<String, Object> data;

	public GetResult(boolean found, Map<String, Object> data) {
		this.found = found;
		this.data = data;
	}

	@Override
	public String toString() {
		return "GetResult [found=" + found + ", data=" + data + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + (found ? 1231 : 1237);
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
		GetResult other = (GetResult) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (found != other.found)
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public static GetResult create(Object object) {
		boolean found = object instanceof Map<?, ?>;
		Map<String, Object> data = found ? (Map<String, Object>) object : Collections.<String, Object> emptyMap();
		return new GetResult(found, data);
	}
}

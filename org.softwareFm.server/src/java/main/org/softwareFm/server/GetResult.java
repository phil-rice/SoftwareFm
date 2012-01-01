package org.softwareFm.server;

import java.util.Map;

import org.softwareFm.utilities.maps.Maps;

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

	public static GetResult notFound() {
		return new GetResult(false, Maps.emptyStringObjectMap());

	}

	@SuppressWarnings("unchecked")
	public static GetResult create(Object object) {
		if (object == null)
			throw new NullPointerException();
		Map<String, Object> map = (Map<String, Object>) object;
		return new GetResult(true, map);
	}
}

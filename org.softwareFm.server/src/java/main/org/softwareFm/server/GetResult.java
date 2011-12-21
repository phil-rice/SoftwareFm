package org.softwareFm.server;

import java.util.Map;

import org.softwareFm.utilities.maps.Maps;

public class GetResult {
	public final boolean found;
	public final Map<String, Object> data;
	public long created;

	public GetResult(boolean found, Map<String, Object> data) {
		this.found = found;
		this.data = data;
		this.created = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "GetResult [found=" + found + ", data=" + data + ", created=" + created + "]";
	}


	public static GetResult notFound() {
		return new GetResult(false, Maps.emptyStringObjectMap());
		
	}
	@SuppressWarnings("unchecked")
	public static GetResult create(Object object) {
		if (object == null)
			throw new NullPointerException();
		return new GetResult(true, (Map<String, Object>) object);
	}
}

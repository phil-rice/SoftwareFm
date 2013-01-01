package org.softwarefm.httpServer.routeMatchers.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwarefm.httpServer.routeMatchers.IRouteMatcher;
import org.softwarefm.utilities.http.HttpMethod;

public class StarRouteMatcher implements IRouteMatcher {

	private final HttpMethod method;

	public StarRouteMatcher(HttpMethod method) {
		this.method = method;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> accept(HttpMethod method, String uri, List<String> fragments) {
		if (this.method != method)
			return null;
		return Collections.EMPTY_MAP;
	}

}

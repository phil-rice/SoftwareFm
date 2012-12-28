package org.softwarefm.httpServer.routeMatchers.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarefm.httpServer.routeMatchers.IRouteMatcher;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.strings.Strings;

public class SquareBracketsMatcher implements IRouteMatcher {

	private final Map<Integer, String> parameters = new HashMap<Integer, String>();
	private final List<String> routeFragments;
	private final HttpMethod method;

	public SquareBracketsMatcher(HttpMethod method, String route) {
		this.method = method;
		routeFragments = Strings.splitIgnoreBlanks(route, "/");

		for (int i = 0; i < routeFragments.size(); i++) {
			String fragment = routeFragments.get(i);
			if (fragment.startsWith("[")) {
				if (!fragment.endsWith("]"))
					throw new IllegalArgumentException("Cannot parse " + route + " as " + fragment + " has no ]");
				String parameter = fragment.substring(1, fragment.length() - 1);
				if (parameter.contains("[") || parameter.contains("]"))
					throw new IllegalArgumentException("Cannot parse " + route + " as " + fragment + " has extra []s");
				parameters.put(i, parameter);
			}
		}
	}

	@Override
	public Map<String, String> accept(HttpMethod method, String uri, List<String> fragments) {
		if (this.method != method)
			return null;
		if (fragments.size() != routeFragments.size())
			return null;
		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < routeFragments.size(); i++) {
			String parameter = parameters.get(i);
			String fragment = fragments.get(i);
			if (parameter != null)
				result.put(parameter, fragment);
			else if (!routeFragments.get(i).equals(fragment))
				return null;
		}
		return result;
	}

}

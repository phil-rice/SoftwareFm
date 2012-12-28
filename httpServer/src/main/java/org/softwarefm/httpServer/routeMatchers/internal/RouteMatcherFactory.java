package org.softwarefm.httpServer.routeMatchers.internal;

import org.softwarefm.httpServer.routeMatchers.IRouteMatcher;
import org.softwarefm.httpServer.routeMatchers.IRouteMatcherFactory;
import org.softwarefm.utilities.http.HttpMethod;

public class RouteMatcherFactory implements IRouteMatcherFactory {

	@Override
	public IRouteMatcher makeMatcherFor(HttpMethod method, String route) {
		if (route.equals("*"))
			return new StarRouteMatcher(method);
		if (route.contains("["))
			return new SquareBracketsMatcher(method, route);
		return null;
	}

}

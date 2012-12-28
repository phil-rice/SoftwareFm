package org.softwarefm.httpServer.routeMatchers;

import org.softwarefm.httpServer.routeMatchers.internal.RouteMatcherFactory;
import org.softwarefm.utilities.http.HttpMethod;

public interface IRouteMatcherFactory {

	IRouteMatcher makeMatcherFor(HttpMethod method ,String route);
	
	public static class Utils{
		public static IRouteMatcherFactory factory(){
			return new RouteMatcherFactory();
		}
	}
}

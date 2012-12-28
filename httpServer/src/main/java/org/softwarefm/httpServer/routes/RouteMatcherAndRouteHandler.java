package org.softwarefm.httpServer.routes;

import org.softwarefm.httpServer.routeMatchers.IRouteMatcher;

public class RouteMatcherAndRouteHandler {

	public final IRouteMatcher routeMatcher;
	public final IRouteHandler routeHandler;

	public RouteMatcherAndRouteHandler(IRouteMatcher routeMatcher, IRouteHandler routeHandler) {
		super();
		this.routeMatcher = routeMatcher;
		this.routeHandler = routeHandler;
	}

	@Override
	public String toString() {
		return "RouteMatcherAndRouteHandler [routeMatcher=" + routeMatcher + ", routeHandler=" + routeHandler + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((routeHandler == null) ? 0 : routeHandler.hashCode());
		result = prime * result + ((routeMatcher == null) ? 0 : routeMatcher.hashCode());
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
		RouteMatcherAndRouteHandler other = (RouteMatcherAndRouteHandler) obj;
		if (routeHandler == null) {
			if (other.routeHandler != null)
				return false;
		} else if (!routeHandler.equals(other.routeHandler))
			return false;
		if (routeMatcher == null) {
			if (other.routeMatcher != null)
				return false;
		} else if (!routeMatcher.equals(other.routeMatcher))
			return false;
		return true;
	}

}

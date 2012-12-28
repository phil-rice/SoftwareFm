package org.softwarefm.httpServer;

import org.apache.http.HttpEntity;
import org.softwarefm.httpServer.internal.HttpRegistry;
import org.softwarefm.httpServer.routeMatchers.IRouteMatcherFactory;
import org.softwarefm.httpServer.routes.IRouteHandler;
import org.softwarefm.utilities.http.HttpMethod;

public interface IHttpRegistry {
	void register(HttpMethod method, IRouteHandler routeHandler, String routePattern, String... params);

	/** This method exists for testing. It is executed in the same thread as the calling thread */
	StatusAndEntity process(HttpMethod method, String uri, HttpEntity entity) throws Exception;

	public static class Utils {
		public static IHttpRegistry registry(){
			return new HttpRegistry(IRouteMatcherFactory.Utils.factory());
		}
	}
	
}

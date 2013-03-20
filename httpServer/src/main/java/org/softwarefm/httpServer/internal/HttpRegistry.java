package org.softwarefm.httpServer.internal;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.softwarefm.httpServer.IHttpRegistry;
import org.softwarefm.httpServer.IRegistryConfigurator;
import org.softwarefm.httpServer.StatusAndEntity;
import org.softwarefm.httpServer.routeMatchers.IRouteMatcher;
import org.softwarefm.httpServer.routeMatchers.IRouteMatcherFactory;
import org.softwarefm.httpServer.routes.IRouteHandler;
import org.softwarefm.httpServer.routes.IRouteHandlerWithParameters;
import org.softwarefm.httpServer.routes.RouteMatcherAndRouteHandler;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.maps.Maps;
import org.softwarefm.utilities.strings.Strings;

public class HttpRegistry implements IHttpRegistry {

	private final Logger logger = Logger.getLogger(getClass().getName());
	private final List<RouteMatcherAndRouteHandler> routes = new ArrayList<RouteMatcherAndRouteHandler>();
	private final IRouteMatcherFactory factory;

	public HttpRegistry(IRouteMatcherFactory factory) {
		super();
		this.factory = factory;
	}

	public void register(HttpMethod method, IRouteHandler routeHandler, String routePattern, String... params) {
		IRouteMatcher matcher = factory.makeMatcherFor(method, IRegistryConfigurator.Utils.defn(routePattern, params));
		routes.add(new RouteMatcherAndRouteHandler(matcher, routeHandler));
	} 

	public StatusAndEntity process(HttpMethod method, String uri, HttpEntity httpEntity) throws Exception {
		List<String> routeFragments = Strings.splitIgnoreBlanks(uri, "/");
		Map<String, String> parameters = new HashMap<String, String>();
		IRouteHandler routeHandler = findRouteHandlerAndUpdateParameters(parameters, method, uri, routeFragments);
		logger.info(MessageFormat.format("Processing {0} {1} {2} {3}", method, uri, routeHandler.getClass().getName(), parameters));
		if (routeHandler instanceof IRouteHandlerWithParameters) 
			addParameters(parameters, httpEntity);

		StatusAndEntity statusAndEntity = routeHandler.execute(method, parameters, httpEntity);
		logger.info(MessageFormat.format("   result{0}",statusAndEntity));
		return statusAndEntity;
	}

	private IRouteHandler findRouteHandlerAndUpdateParameters(Map<String, String> parameters, HttpMethod method, String uri, List<String> routeFragments) {
		for (RouteMatcherAndRouteHandler handler : routes) {
			Map<String, String> accept = handler.routeMatcher.accept(method, uri, routeFragments);
			if (accept != null) {
				parameters.putAll(accept);
				return handler.routeHandler;
			}
		}
		throw new IllegalArgumentException("Don't know how to handle " + method + " " + uri);
	}

	private void addParameters(Map<String, String> parameters, HttpEntity httpEntity) {
		try {
			if (httpEntity != null) {
				List<NameValuePair> pairs = URLEncodedUtils.parse(httpEntity);
				for (NameValuePair pair : pairs)
					Maps.putNoDuplicates(parameters, pair.getName(), pair.getValue());
			}
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

}

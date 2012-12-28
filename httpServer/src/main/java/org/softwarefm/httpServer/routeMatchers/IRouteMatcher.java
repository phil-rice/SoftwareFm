package org.softwarefm.httpServer.routeMatchers;

import java.util.List;
import java.util.Map;

import org.softwarefm.utilities.http.HttpMethod;

public interface IRouteMatcher {

	/** returns null, or a map of parameters. If null, the matcher didn't match */
	Map<String, String> accept(HttpMethod method, String uri, List<String> fragments);


}

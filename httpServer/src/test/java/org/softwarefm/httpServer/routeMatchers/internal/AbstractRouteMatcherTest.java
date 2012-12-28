package org.softwarefm.httpServer.routeMatchers.internal;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.softwarefm.httpServer.routeMatchers.IRouteMatcher;
import org.softwarefm.httpServer.routeMatchers.IRouteMatcherFactory;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.maps.Maps;
import org.softwarefm.utilities.strings.Strings;

abstract public class AbstractRouteMatcherTest<M extends IRouteMatcher> extends TestCase {

	protected RouteMatcherFactory factory = (RouteMatcherFactory) IRouteMatcherFactory.Utils.factory();

	protected void checkDoesntMatch(String route, String uri) {
		for (HttpMethod method : HttpMethod.values())
			checkDoesntMatch(route, method, uri);

	}

	@SuppressWarnings("unchecked")
	protected void checkDoesntMatch(String route, HttpMethod method, String uri) {
		M matcher = (M) factory.makeMatcherFor(method, route);
		Map<String, String> actual = matcher.accept(method, uri, Strings.splitIgnoreBlanks(uri, "/"));
		assertNull(actual);

	}

	protected void checkMatches(String route, String uri, Object... expectedParameters) {
		for (HttpMethod method : HttpMethod.values())
			checkMatches(route, method, uri, expectedParameters);
	}

	@SuppressWarnings("unchecked")
	protected void checkMatches(String route, HttpMethod method, String uri, Object... expectedParameters) {
		M matcher = (M) factory.makeMatcherFor(method, route);
		Map<String, String> expected = Maps.makeMap(expectedParameters);
		List<String> fragments = Strings.splitIgnoreBlanks(uri, "/");
		Map<String, String> actual = matcher.accept(method, uri, fragments);
		assertEquals(expected, actual);

		for (HttpMethod otherMethod : HttpMethod.values())
			if (otherMethod != method)
				assertNull(matcher.accept(otherMethod, uri, fragments));

	}

}

package org.softwarefm.httpServer.routeMatchers.internal;


public class StarRouteMatcherTest extends AbstractRouteMatcherTest<StarRouteMatcher> {

	public void testStarMatchesEverything() {
		checkMatches("*", "any/thing");
		checkMatches("*",  "any/thi/ng");
		checkMatches("*",  "anything");
		checkMatches("*",  "anything.*");
		checkMatches("*",  "any/*/thing");
	}
}

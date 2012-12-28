package org.softwarefm.httpServer.routeMatchers.internal;

public class SquareBracketsMatcherTest extends AbstractRouteMatcherTest<SquareBracketsMatcher>{

	public void testMatchesIfNonSquaresMatchAndThingsInSquaresAreAddedToParameters(){
		checkMatches("a/[b]/c/[d]", "a/1/c/2", "b", "1", "d", "2");
		checkMatches("a/[b]/c/[d]", "a/b/c/d", "b", "b", "d", "d");
		checkMatches("a/[id]", "a/b", "id", "b");
	}
	
	public void testDoesntMatchIfMarkersNotPresent(){
		checkDoesntMatch("a/[b]/c/[d]", "a/b/c/d/e");//too big
		checkDoesntMatch("a/[b]/c/[d]", "a/b/x/d");
	}
	
}

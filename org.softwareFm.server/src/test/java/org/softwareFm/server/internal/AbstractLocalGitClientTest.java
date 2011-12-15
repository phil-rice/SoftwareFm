package org.softwareFm.server.internal;

import org.softwareFm.utilities.maps.Maps;

abstract public class AbstractLocalGitClientTest extends GitTest {

	public void testGetWhenNothingThere() {
		checkNoData("a/b/c");
	}

	public void testWhenDataPresent() {
		put("a/b/c", a1b2);
		checkLocalGet("a/b/c", a1b2);
	}

	public void testPopulatesChildrenButNotGrandchildren() {
		setUpABC(a1b2);

		checkLocalGet("a/b/c", Maps.with(a1b2, //
				"col1", Maps.stringObjectMap("v11", v11, "v12", v12),//
				"col2", Maps.stringObjectMap("v21", v21, "v22", v22)));
	}

	public void testPost(){
		localGitClient.post("a/b/c", a1b2);
		checkLocalGet("a/b/c", a1b2);
	}
}

package org.softwareFm.server.internal;

import org.softwareFm.server.ILocalGitClient;
import org.softwareFm.utilities.maps.Maps;

abstract public class AbstractLocalGitClientTest extends GitTest {

	private ILocalGitClient client;

	abstract protected ILocalGitClient makeLocalGitClient();

	public void testGetWhenNothingThere() {
		checkNoData(client, "a/b/c");
	}

	public void testWhenDataPresent() {
		put("a/b/c", a1b2);
		checkLocalGet(client, "a/b/c", a1b2);
	}

	public void testPopulatesChildrenButNotGrandchildren() {
		setUpABC(a1b2);

		checkLocalGet(client, "a/b/c", Maps.with(a1b2, //
				"col1", Maps.with(typeCollectionMap, "v11", v11, "v12", v12),//
				"col2", Maps.with(typeCollectionMap, "v21", v21, "v22", v22)));
	}

	public void testPost() {
		gitFacard.createRepository(root, "a");
		client.post("a/b/c", a1b2);
		checkLocalGet(client, "a/b/c", a1b2);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		client = makeLocalGitClient();
	}

}

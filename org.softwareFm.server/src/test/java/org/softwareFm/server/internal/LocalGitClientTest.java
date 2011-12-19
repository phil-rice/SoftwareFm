package org.softwareFm.server.internal;

import java.io.File;

import org.softwareFm.server.GetResult;
import org.softwareFm.utilities.maps.Maps;

public class LocalGitClientTest extends GitTest {

	private LocalGitClient client;

	public void testLocalGetReturnsDataFileModifiedByCollections() {
		put(localRoot, "a/b/c", v11);
		checkLocalGet(client, "a/b/c", v11);
		put(localRoot, "a/b/c/d/e", v12);
		put(localRoot, "a/b/c/d/e/f/g/h", v12);// this is ignored as it is too deep
		put(localRoot, "a/b/c/i/j", v12);
		checkLocalGet(client, "a/b/c", Maps.with(v11, "d", Maps.stringObjectMap("e", v12), "i", Maps.stringObjectMap("j", v12)));
	}

	public void testLocalGetReturnsNotFoundIdDirectoryNotPresent() {
		GetResult result = client.localGet("a/b/c");
		assertFalse(result.found);
		assertEquals(emptyMap, result.data);
	}

	public void testLocalGetReturnsEmptyMapModifiedByCollectionsIfNoDataFile() {
		new File(localRoot, "a/b/c").mkdirs();
		checkLocalGet(client, "a/b/c", emptyMap);
		put(localRoot, "a/b/c/d/e", v12);
		put(localRoot, "a/b/c/d/e/f/g/h", v12);// this is ignored as it is too deep
		put(localRoot, "a/b/c/i/j", v12);
		checkLocalGet(client, "a/b/c", Maps.with(emptyMap, "d", Maps.stringObjectMap("e", v12), "i", Maps.stringObjectMap("j", v12)));

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		client = new LocalGitClient(localRoot);
	}

}

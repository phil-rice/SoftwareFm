package org.softwareFm.server.internal;

import java.io.File;

import org.softwareFm.server.GetResult;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.utilities.maps.Maps;

public class LocalGitClientTest extends GitTest {

	private LocalGitClient client;

	public void testLocalGetReturnsDataFileModifiedBySubDirectories() {
		put(localRoot, "a/b/c", v11);
		checkLocalGet(client, "a/b/c", v11);
		put(localRoot, "a/b/c/d/e", v12);
		put(localRoot, "a/b/c/d/e/f/g/h", v12);// this is ignored as it is too deep
		put(localRoot, "a/b/c/i/j", v12);
		checkLocalGet(client, "a/b/c", Maps.with(v11, "d", Maps.stringObjectMap("e", v12), "i", Maps.stringObjectMap("j", v12)));
	}

	public void testLocalGetReturnsDataFileModifiedByDataFilesInSubDirectory() {
		put(localRoot, "a/b/c", v11);
		put(localRoot, "a/b/c/d", v12);
		put(localRoot, "a/b/c/d/e", v21);
		put(localRoot, "a/b/c/f", v22);

		checkLocalGet(client, "a/b/c", Maps.with(v11, "d", Maps.with(v12, "e", v21), "f", v22));

	}

	public void testLocalGetReturnsNotFoundIdDirectoryNotPresent() {
		GetResult result = client.localGet(IFileDescription.Utils.plain("a/b/c"));
		assertFalse(result.found);
		assertEquals(emptyMap, result.data);
	}

	public void testLocalGetAvoidDirectoriesThatStartWithDot() {
		new File(localRoot, "a/b/c").mkdirs();
		checkLocalGet(client, "a/b/c", emptyMap);
		put(localRoot, "a/b/c/.git", v11);
		put(localRoot, "a/b/c/d/.anotherName", v12);
		put(localRoot, "a/b/c/d/e/f/g/h", v12);// this is ignored as it is too deep
		put(localRoot, "a/b/c/i/j", v12);
		checkLocalGet(client, "a/b/c", Maps.with(emptyMap, "d", Maps.emptyStringObjectMap(), "i", Maps.stringObjectMap("j", v12)));

	}

	public void testLocalGetReturnsEmptyMapModifiedByCollectionsIfNoDataFile() {
		new File(localRoot, "a/b/c").mkdirs();
		checkLocalGet(client, "a/b/c", emptyMap);
		put(localRoot, "a/b/c/d/e", v12);
		put(localRoot, "a/b/c/d/e/f/g/h", v12);// this is ignored as it is too deep
		put(localRoot, "a/b/c/i/j", v12);
		checkLocalGet(client, "a/b/c", Maps.with(emptyMap, "d", Maps.stringObjectMap("e", v12), "i", Maps.stringObjectMap("j", v12)));

	}

	public void testGetFileDoesntIncludeSubdirectories() {
		put(localRoot, "a/b/c", v11);
		checkGetFile(client, "a/b/c", v11);
		put(localRoot, "a/b/c/d/e", v12);
		put(localRoot, "a/b/c/d/e/f/g/h", v12);
		put(localRoot, "a/b/c/i/j", v12);
		checkGetFile(client, "a/b/c", v11);
	}

	public void testGetFileReturnsEmptyMapIfDataFileDoesntExistButDirectoryDoes() {
		new File(localRoot, "a/b/c").mkdirs();
		checkGetFile(client, "a/b/c", emptyMap);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		client = new LocalGitClient(localRoot);
	}

}

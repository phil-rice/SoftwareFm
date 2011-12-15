package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.server.GetResult;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.services.IServiceExecutor;

public abstract class GitTest extends TestCase {
	protected final static Map<String, Object> a1b2 = Maps.stringObjectMap("a", 1L, "b", 2L);

	protected final static Map<String, Object> typeCollectionMap = Maps.stringObjectMap(ServerConstants.typeTag, ServerConstants.collectionType);
	protected final static Map<String, Object> v11 = Maps.stringObjectMap("c", 1l, "v", 1l);
	protected final static Map<String, Object> v12 = Maps.stringObjectMap("c", 1l, "v", 2l);
	protected final static Map<String, Object> v21 = Maps.stringObjectMap("c", 2l, "v", 1l);
	protected final static Map<String, Object> v22 = Maps.stringObjectMap("c", 2l, "v", 2l);

	protected File root;
	protected LocalGitClient localGitClient;
	private IServiceExecutor serviceExecutor;

	protected void put(String url, Map<String, Object> data) {
		put(root, url, data);
	}

	protected void put(File root, String url, Map<String, Object> data) {
		String text = Json.toString(data);
		File directory = new File(root, url);
		directory.mkdirs();
		File file = new File(directory, ServerConstants.dataFileName);
		Files.setText(file, text);
	}

	protected void setUpABC(Map<String, Object> data) {
		setUpABC(root, data);
	}

	protected void setUpABC(File root, Map<String, Object> data) {
		put(root, "a/b/c", data);

		put(root, "a/b/c/col1", typeCollectionMap);
		put(root, "a/b/c/col1/v11", v11);
		put(root, "a/b/c/col1/v12", v12);
		put(root, "a/b/c/col1/v11/col1_v1_1", typeCollectionMap);
		put(root, "a/b/c/col1/v11/col1_v1_2", typeCollectionMap);

		put(root, "a/b/c/col2", typeCollectionMap);
		put(root, "a/b/c/col2/v21", v21);
		put(root, "a/b/c/col2/v22", v22);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String tempDir = System.getProperty("java.io.tmpdir");
		assertTrue(!tempDir.equals(""));
		File tests = new File(tempDir, "softwareFmTests");
		tests.mkdirs();
		root = new File(tests, getClass().getSimpleName());
		Files.deleteDirectory(root);
		localGitClient = makeLocalGitClient();
	}

	protected LocalGitClient makeLocalGitClient() {
		return new LocalGitClient(root);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (serviceExecutor != null)
			serviceExecutor.shutdown();
	}

	protected void checkNoData(String url) {
		GetResult result = localGitClient.get(url);
		assertFalse(result.toString(), result.found);

	}

	protected void checkLocalGet(String url, Map<String, Object> data) {
		GetResult result = localGitClient.get(url);
		assertTrue(result.found);
		assertEquals(data, result.data);
	}

	protected IServiceExecutor getServiceExecutor() {
		return serviceExecutor == null ? serviceExecutor = IServiceExecutor.Utils.defaultExecutor() : serviceExecutor;
	}

}

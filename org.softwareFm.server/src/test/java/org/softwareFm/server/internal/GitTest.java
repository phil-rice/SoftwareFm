package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.eclipse.jgit.storage.file.FileRepository;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ILocalGitClient;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwareFm.utilities.tests.Tests;

public abstract class GitTest extends TestCase {
	protected final static Map<String, Object> a1b2 = Maps.stringObjectLinkedMap("a", 1L, "b", 2L);

	protected final static Map<String, Object> typeCollectionMap = Maps.stringObjectLinkedMap(ServerConstants.typeTag, ServerConstants.collectionType);
	protected final static Map<String, Object> v11 = Maps.stringObjectLinkedMap("c", 1l, "v", 1l);
	protected final static Map<String, Object> v12 = Maps.stringObjectLinkedMap("c", 1l, "v", 2l);
	protected final static Map<String, Object> v21 = Maps.stringObjectLinkedMap("c", 2l, "v", 1l);
	protected final static Map<String, Object> v22 = Maps.stringObjectLinkedMap("c", 2l, "v", 2l);

	protected static final Map<String, Object> emptyMap = Maps.stringObjectMap();

	private IServiceExecutor serviceExecutor;
	protected final IGitFacard gitFacard = IGitFacard.Utils.makeFacard();

	private IHttpClient httpClient;

	protected File root;
	protected File localRoot;
	protected File remoteRoot;
	protected String remoteAsUri;

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
		root = Tests.makeTempDirectory(getClass().getSimpleName());
		localRoot = new File(root, "local");
		remoteRoot = new File(root, "remote");
		remoteAsUri = new File(root, "remote").getAbsolutePath();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (serviceExecutor != null)
			serviceExecutor.shutdownAndAwaitTermination(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		if (httpClient != null)
			httpClient.shutdown();
	}

	protected void checkNoData(ILocalGitClient client, String url) {
		GetResult result = client.localGet(url);
		assertFalse(result.toString(), result.found);

	}

	protected void checkLocalGet(ILocalGitClient client, String url, Map<String, Object> data) {
		GetResult result = client.localGet(url);
		assertTrue(result.found);
		assertEquals(data, result.data);
	}
	protected void checkGetFile(ILocalGitClient client, String url, Map<String, Object> data) {
		GetResult result = client.getFile(url);
		assertTrue(result.found);
		assertEquals(data, result.data);
	}

	protected IServiceExecutor getServiceExecutor() {
		return serviceExecutor == null ? serviceExecutor = IServiceExecutor.Utils.defaultExecutor() : serviceExecutor;
	}

	protected IHttpClient getHttpClient() {
		return httpClient == null ? httpClient = IHttpClient.Utils.builder("localhost", ServerConstants.testPort) : httpClient;
	}

	
	protected void checkCreateRepository(IGitServer gitServer, String url) {
		gitServer.createRepository(url);
		FileRepository fileRepository = gitFacard.makeFileRepository(gitServer.getRoot(), url);
		assertEquals(new File(gitServer.getRoot(), url + "/" + ServerConstants.DOT_GIT), fileRepository.getDirectory());
	}

	protected IGitServer makeGitServer(String remoteUriPrefix) {
		return new GitServer(gitFacard, root, remoteUriPrefix);
	}

	protected void checkRepositoryExists(File repo) {
		assertTrue(new File(repo, ServerConstants.DOT_GIT).exists());
	}

	protected void checkRepositoryDoesntExists(File repo) {
		assertFalse(new File(repo, ServerConstants.DOT_GIT).exists());
	}

	protected void checkContents(File root, String url, Map<String, Object> data) {
		File directory = new File(root, url);
		assertTrue(directory.exists());
		File file = new File(directory, ServerConstants.dataFileName);
		assertEquals(Json.mapFromString(Files.getText(file)), data);
	}

}

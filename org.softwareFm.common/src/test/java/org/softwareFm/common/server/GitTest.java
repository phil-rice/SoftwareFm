package org.softwareFm.common.server;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IGitWriter;
import org.softwareFm.common.IRepoFinder;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.internal.GitOperations;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.url.Urls;

public abstract class GitTest extends TemporaryFileTest {
	protected final static Map<String, Object> a1b2 = Maps.stringObjectLinkedMap("a", 1L, "b", 2L);

	protected final static Map<String, Object> typeCollectionMap = Maps.stringObjectLinkedMap(CommonConstants.typeTag, CommonConstants.collectionType);
	protected final static Map<String, Object> v11 = Maps.stringObjectLinkedMap("c", 1l, "v", 1l);
	protected final static Map<String, Object> v12 = Maps.stringObjectLinkedMap("c", 1l, "v", 2l);
	protected final static Map<String, Object> v21 = Maps.stringObjectLinkedMap("c", 2l, "v", 1l);
	protected final static Map<String, Object> v22 = Maps.stringObjectLinkedMap("c", 2l, "v", 2l);
	protected final static Map<String, Object> v31 = Maps.stringObjectLinkedMap("c", 3l, "v", 1l);
	protected final static Map<String, Object> v32 = Maps.stringObjectLinkedMap("c", 3l, "v", 2l);
	protected final static Map<String, Object> v41 = Maps.stringObjectLinkedMap("c", 4l, "v", 1l);
	protected final static Map<String, Object> v42 = Maps.stringObjectLinkedMap("c", 4l, "v", 2l);

	protected static final Map<String, Object> emptyMap = Maps.stringObjectMap();

	protected IServiceExecutor serviceExecutor;

	protected IGitOperations localOperations;
	protected IGitOperations remoteOperations;

	protected IGitLocal gitLocal;

	protected File localRoot;
	protected File remoteRoot;
	protected String remoteAsUri;

	protected IRepoFinder repoFinder;

	private IGitWriter gitWriter;

	protected void put(String url, Map<String, Object> data) {
		put(root, url, data);
	}

	protected void put(File root, String url, Map<String, Object> data) {
		put(root, IFileDescription.Utils.plain(url), data);
	}

	protected void put(File root, IFileDescription fileDescription, Map<String, Object> data) {
		File directory = fileDescription.getDirectory(root);
		directory.mkdirs();
		File file = fileDescription.getFile(root);
		String text = fileDescription.encode(data);
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
		localRoot = new File(root, "local");
		remoteRoot = new File(root, "remote");
		remoteAsUri = new File(root, "remote").getAbsolutePath();
		localOperations = IGitOperations.Utils.gitOperations(localRoot);
		remoteOperations = IGitOperations.Utils.gitOperations(remoteRoot);
		repoFinder = makeFindRepositoryRoot();
		gitWriter = new GitWriterForTests(remoteOperations);
		gitLocal = IGitLocal.Utils.localReader(repoFinder, localOperations,  gitWriter, remoteRoot.getAbsolutePath(), 500);
	}

	protected IRepoFinder makeFindRepositoryRoot() {
		return IRepoFinder.Utils.forTests(remoteOperations);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (serviceExecutor != null)
			serviceExecutor.shutdownAndAwaitTermination(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

	}

	protected void checkNoData(IGitLocal reader, String url) {
		IFileDescription plain = IFileDescription.Utils.plain(url);
		assertNull(reader.getFile(plain));
	}

	protected void checkGetFileAndDescendants(IGitLocal reader, String url, Map<String, Object> data) {
		IFileDescription fileDescription = IFileDescription.Utils.plain(url);
		checkGetFileAndDescendants(reader, fileDescription, data);
	}

	protected void checkGetFileAndDescendants(IGitLocal reader, IFileDescription fileDescription, Map<String, Object> data) {
		assertEquals(data, reader.getFileAndDescendants(fileDescription));
	}

	protected void checkGetFile(IGitLocal reader, String url, Map<String, Object> data) {
		checkGetFile(reader, IFileDescription.Utils.plain(url), data);
	}

	protected void checkGetFile(IGitLocal reader, IFileDescription fileDescription, Map<String, Object> data) {
		assertEquals(data, reader.getFile(fileDescription));
	}

	protected IServiceExecutor getServiceExecutor() {
		return serviceExecutor == null ? serviceExecutor = IServiceExecutor.Utils.defaultExecutor() : serviceExecutor;
	}

	protected void checkCreateRepository(final IGitOperations gitOperations, final String url) {
		gitOperations.init(url);
		File root = ((GitOperations) gitOperations).root;
		checkRepositoryExists(root, url);
	}

	protected void checkRepositoryExists(File root, String url) {
		File dotGitDirectory = new File(root, Urls.compose(url, CommonConstants.DOT_GIT));
		assertTrue(dotGitDirectory.exists());
		assertTrue(dotGitDirectory.isDirectory());
	}

	protected void checkRepositoryDoesntExists(File repo) {
		assertFalse(new File(repo, CommonConstants.DOT_GIT).exists());
	}

	protected void checkContents(File root, String url, Map<String, Object> data) {
		File directory = new File(root, url);
		assertTrue(directory.exists());
		File file = new File(directory, CommonConstants.dataFileName);
		assertEquals(Json.mapFromString(Files.getText(file)), data);
	}

	protected void createAndPull(String url) {
		localOperations.init(url);
		localOperations.setConfigForRemotePull(url, remoteRoot.getAbsolutePath());
		localOperations.pull(url);
	}

}

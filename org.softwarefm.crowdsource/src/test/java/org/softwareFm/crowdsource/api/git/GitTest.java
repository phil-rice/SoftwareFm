/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.git;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.git.internal.GitOperations;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.url.Urls;

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

	protected File localRoot;
	protected File remoteRoot;
	protected String remoteAsUri;

	protected IRepoFinder repoFinder;


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
		repoFinder = makeRepoFinder();
	}

	protected IRepoFinder makeRepoFinder() {
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

	protected void checkGetFileAndDescendants(IContainer container, int depth, String url, Map<String, Object> data) {
		IFileDescription fileDescription = IFileDescription.Utils.plain(url);
		assertEquals(data, IGitReader.Utils.getFileAndDescendants(container, fileDescription, depth));
	}

	protected void checkGetFileAndDescendants(IContainer container, int depth, IFileDescription fileDescription, Map<String, Object> data) {
		assertEquals(data, IGitReader.Utils.getFileAndDescendants(container, fileDescription, depth));
	}

	protected void checkGetFile(IContainer container, String url, Map<String, Object> data) {
		checkGetFile(container, IFileDescription.Utils.plain(url), data);
	}

	protected void checkGetFile(IContainer container, IFileDescription fileDescription, Map<String, Object> data) {
		assertEquals(data, IGitReader.Utils.getFileAsMap(container, fileDescription));
	}

	protected IServiceExecutor getServiceExecutor() {
		return serviceExecutor == null ? serviceExecutor = IServiceExecutor.Utils.defaultExecutor("Test-{0}") : serviceExecutor;
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
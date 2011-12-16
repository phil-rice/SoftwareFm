package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.CheckRepositoryFacardCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.tests.IIntegrationTest;
import org.softwareFm.utilities.tests.Tests;

public class GitRepositoryFacardIntegrationTest extends GitTest implements IIntegrationTest {

	private ISoftwareFmServer server;
	private GitRepositoryFacard repositoryFacard;
	private IGitServer remoteGitServer;
	private File remoteRoot;

	public void testGetWhenDataExists() throws InterruptedException, ExecutionException {
		File directory = new File(root, "a/b/c");
		directory.mkdirs();
		File file = new File(directory, ServerConstants.dataFileName);
		Files.setText(file, Json.toString(v11));
		CheckRepositoryFacardCallback callback = IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, v11);
		repositoryFacard.get("a/b/c", callback).get();
		callback.assertCalled();
	}

	@Override
	public void testGetWhenDataDoesntExist() throws Exception {
		File directory = new File(root, "a/b/c");
		File file = new File(directory, ServerConstants.dataFileName);
		remoteGitServer.createRepository("a/b");
		remoteGitServer.post("a/b/c", v11); // so now the remote repository has the data

		assertTrue(!file.exists()); // but there is no local copy
		CheckRepositoryFacardCallback callback = IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, v11);
		repositoryFacard.get("a/b/c", callback).get();
		callback.assertCalled();
		assertTrue(file.exists()); // and now there is a local copy
		checkFileMatches(file, v11);
	}

	private void checkFileMatches(File file, Map<String, Object> expected) {
		assertEquals(expected, Json.parse(Files.getText(file)));
	}

	public void testPostThenGet() throws InterruptedException, ExecutionException {
		remoteGitServer.createRepository("a/b");
		File localFile = new File(new File(root, "a/b/c"), ServerConstants.dataFileName);
		File remoteFile = new File(new File(remoteRoot, "a/b/c"), ServerConstants.dataFileName);
		repositoryFacard.post("a/b/c", v11, IResponseCallback.Utils.noCallback()).get();

		checkFileMatches(localFile, v11);
		checkFileMatches(remoteFile, v11);
		repositoryFacard.get("a/b/c", IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, v11)).get();

		repositoryFacard.post("a/b/c", v12, IResponseCallback.Utils.noCallback()).get();
		checkFileMatches(remoteFile, v12);
		checkFileMatches(localFile, v12);
		repositoryFacard.get("a/b/c", IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, v12)).get();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		remoteRoot = Tests.makeTempDirectory(getClass().getSimpleName() + "_remote");
		remoteGitServer = IGitServer.Utils.gitServer(remoteRoot);
		IProcessCall processCall = IProcessCall.Utils.softwareFmProcessCall(remoteGitServer);
		server = ISoftwareFmServer.Utils.server(ServerConstants.testPort, 2, processCall, ICallback.Utils.<Throwable> memory());
		repositoryFacard = new GitRepositoryFacard(getHttpClient(), getServiceExecutor(), IGitFacard.Utils.makeFacard(), localGitClient, remoteRoot.getAbsolutePath());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (server != null)
			server.shutdown();
	}

}

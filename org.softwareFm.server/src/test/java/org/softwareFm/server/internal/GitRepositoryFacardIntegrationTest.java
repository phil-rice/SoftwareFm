package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.httpClient.requests.CheckCallback;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.CheckRepositoryFacardCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.tests.IIntegrationTest;
import org.softwareFm.utilities.tests.Tests;

public class GitRepositoryFacardIntegrationTest extends GitTest implements IIntegrationTest {

	private ISoftwareFmServer server;
	private GitRepositoryFacard repositoryFacard;
	private IGitServer remoteGitServer;
	private IGitServer localGitServer;

	public void testCanPostAfterLocalRepositoryExists() throws Exception {
		gitFacard.createRepository(remoteRoot, "a/b");
		gitFacard.clone(new File(remoteRoot, "a/b").getAbsolutePath(), localRoot, "a/b");
		repositoryFacard.post("a/b/c", v11, IResponseCallback.Utils.noCallback()).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		CheckRepositoryFacardCallback check = IRepositoryFacardCallback.Utils.checkMatches(200, v11);
		repositoryFacard.get("a/b/c", check).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		check.assertCalled();
	}

	public void testCannotPostIfLocalRepositoryDoesntExist() throws Exception {
		File localRepo = new File(localRoot, "a/b");
		checkRepositoryDoesntExists(localRepo);
		Tests.assertThrowsWithMessage("Cannot post a/b/c when local repository doesn't exist", IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				try {
					repositoryFacard.post("a/b/c", v11, IResponseCallback.Utils.noCallback()).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
		checkRepositoryDoesntExists(localRepo);
	}

	public void testMakeRootClonesLocally() throws Exception {
		File localRepo = new File(localRoot, "a/b");
		checkRepositoryDoesntExists(localRepo);
		repositoryFacard.makeRoot("a/b", IResponseCallback.Utils.noCallback()).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		checkRepositoryExists(localRepo);
	}

	public void testCanPostAfterMakeRoot() throws Exception {
		CheckCallback checkMakeRoot = IResponseCallback.Utils.checkCallback(200, "Made root at location /a/b");
		repositoryFacard.makeRoot("a/b", checkMakeRoot).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		checkMakeRoot.assertCalledSuccessfullyOnce();

		repositoryFacard.post("a/b/c", v11, IResponseCallback.Utils.noCallback()).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		assertTrue(new File(localRoot, "a/b/" + ServerConstants.DOT_GIT).exists());
	}

	public void testPostAlsoClonesIfFirstPost() {

	}

	public void testGetWhenDataExists() throws Exception {
		checkCreateRepository(localGitServer, "a/b");// need a repository, otherwise get 'above repository' behaviour
		File directory = new File(localRoot, "a/b/c");
		directory.mkdirs();
		File file = new File(directory, ServerConstants.dataFileName);
		Files.setText(file, Json.toString(v11));
		CheckRepositoryFacardCallback callback = IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, v11);
		repositoryFacard.get("a/b/c", callback).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		callback.assertCalled();
	}

	public void testCallsBackEvenIfInCache() throws Exception {
		checkCreateRepository(localGitServer, "a/b");// need a repository, otherwise get 'above repository' behaviour
		File directory = new File(localRoot, "a/b/c");
		directory.mkdirs();
		File file = new File(directory, ServerConstants.dataFileName);
		Files.setText(file, Json.toString(v11));
		CheckRepositoryFacardCallback callback1 = IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, v11);
		CheckRepositoryFacardCallback callback2 = IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, v11);
		repositoryFacard.get("a/b/c", callback1).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		repositoryFacard.get("a/b/c", callback2).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		callback1.assertCalled();
		callback2.assertCalled();

	}

	public void testGetWhenDataDoesntExistLocallyButDoesRemotely() throws Exception {
		File directory = new File(localRoot, "a/b/c");
		File file = new File(directory, ServerConstants.dataFileName);
		remoteGitServer.createRepository("a/b");
		remoteGitServer.post("a/b/c", v11); // so now the remote repository has the data

		assertTrue(!file.exists()); // but there is no local copy
		CheckRepositoryFacardCallback callback = IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, v11);
		repositoryFacard.get("a/b/c", callback).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		callback.assertCalled();
		assertTrue(file.exists()); // and now there is a local copy
		checkFileMatches(file, v11);
	}

	public void testGetWhenAboveRepository() throws InterruptedException, ExecutionException, TimeoutException {
		repositoryFacard.makeRoot("a/b/c", IResponseCallback.Utils.noCallback()).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		remoteGitServer.createRepository("a/b/c");
		remoteGitServer.createRepository("a/b/d");
		remoteGitServer.createRepository("a/b/e");

		localGitServer.createRepository("a/b/c");
		checkRepositoryDoesntExists(new File(localRoot, "a/b"));
		CheckRepositoryFacardCallback checkMatches = IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, Maps.stringObjectMap(//
				"c", Maps.emptyStringObjectMap(),//
				"d", Maps.emptyStringObjectMap(),//
				"e", Maps.emptyStringObjectMap()));
		repositoryFacard.get("a/b", checkMatches).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		checkMatches.assertCalled();

	}

	private void checkFileMatches(File file, Map<String, Object> expected) {
		assertEquals(expected, Json.parse(Files.getText(file)));
	}

	public void testPostThenGet() throws InterruptedException, ExecutionException {
		remoteGitServer.createRepository("a/b");
		localGitServer.clone("a/b/");

		File localFile = new File(new File(localRoot, "a/b/c"), ServerConstants.dataFileName);
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
		remoteGitServer = IGitServer.Utils.gitServer(remoteRoot, "not used");
		localGitServer = IGitServer.Utils.gitServer(localRoot, remoteRoot.getAbsolutePath());
		IProcessCall processCall = IProcessCall.Utils.softwareFmProcessCall(remoteGitServer);
		server = ISoftwareFmServer.Utils.server(ServerConstants.testPort, 4, processCall, ICallback.Utils.<Throwable> memory());
		repositoryFacard = new GitRepositoryFacard(getHttpClient(), getServiceExecutor(), localGitServer);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (server != null)
			server.shutdown();
		if (repositoryFacard != null)
			repositoryFacard.shutdown();
	}

}

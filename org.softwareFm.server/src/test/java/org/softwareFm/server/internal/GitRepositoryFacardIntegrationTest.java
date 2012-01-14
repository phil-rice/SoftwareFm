package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.httpClient.requests.CheckCallback;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.CheckRepositoryFacardCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Urls;
import org.softwareFm.utilities.tests.IIntegrationTest;
import org.softwareFm.utilities.tests.Tests;

public class GitRepositoryFacardIntegrationTest extends GitTest implements IIntegrationTest {

	private ISoftwareFmServer server;
	private GitRepositoryFacard repositoryFacard;
	private IGitServer remoteGitServer;
	private IGitServer localGitServer;
	private File localAbc;
	private File remoteAbc;

	public void testDelete() throws Exception {

		checkCreateRepository(remoteGitServer, "a/b");
		put(remoteRoot, "a/b/c", v11);
		gitFacard.addAllAndCommit(remoteRoot, "a/b", "message");
		localGitServer.clone("a/b");
		assertTrue(remoteAbc.exists());
		assertTrue(localAbc.exists());

		repositoryFacard.get("a/b/c", IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, v11)).get();

		CheckCallback callback = IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, "");
		repositoryFacard.delete("a/b/c", callback).get();
		callback.assertCalledSuccessfullyOnce();
		assertFalse(remoteAbc.exists());
		assertFalse(localAbc.exists());

		CheckRepositoryFacardCallback notFound = IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.notFoundStatusCode, Maps.emptyStringObjectMap());
		repositoryFacard.get("a/b/c", notFound).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		notFound.assertCalled();
	}
	
	public void testClearCache() throws Exception{
		checkCreateRepository(remoteGitServer, "a/b");
		File localRespository = new File(localRoot, Urls.compose("a/b", ServerConstants.DOT_GIT));
		put(remoteRoot, "a/b/c", v11);
		gitFacard.addAllAndCommit(remoteRoot, "a/b", "message");
		
		assertFalse(localRespository.exists());
		localGitServer.clone("a/b");
		assertTrue(localRespository.exists());
		assertTrue(remoteAbc.exists());
		assertTrue(localAbc.exists());
		
		repositoryFacard.clearCache("a/b/c");
		assertFalse(localRespository.exists());
		assertTrue(remoteAbc.exists());
		assertFalse(localAbc.exists());

		repositoryFacard.get("a/b/c", IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, v11)).get();
		assertTrue(localRespository.exists());
		assertTrue(remoteAbc.exists());
		assertTrue(localAbc.exists());
	}

	public void testCanSeeAboveRepositoryAfterPost() throws InterruptedException, ExecutionException, TimeoutException {
		File ab = new File(localRoot, "a/b");
		File abc = new File(localRoot, "a/b/c");
		File abd = new File(localRoot, "a/b/d");
		checkRepositoryDoesntExists(ab);
		checkRepositoryDoesntExists(abc);
		checkRepositoryDoesntExists(abd);

		// Now
		repositoryFacard.get("a/b", IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.notFoundStatusCode, Maps.emptyStringObjectMap())).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);

		repositoryFacard.makeRoot("a/b/c", IResponseCallback.Utils.noCallback()).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		repositoryFacard.makeRoot("a/b/d", IResponseCallback.Utils.noCallback()).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);

		final Map<String, Object> expected = Maps.stringObjectMap("c", Maps.emptyStringObjectMap(), "d", Maps.emptyStringObjectMap());
		repositoryFacard.get("a/b", IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, expected)).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);

		final AtomicInteger count = new AtomicInteger();
		repositoryFacard.findRepositoryBaseOrAboveRepositoryData("a/b", new IGetCallback() {
			@Override
			public GetResult aboveRepositoryData(Map<String, Object> data) {
				assertEquals(1, count.incrementAndGet());
				assertEquals(expected, data);
				return null;
			}

			@Override
			public GetResult repositoryBase(String repositoryBase) {
				fail();
				return null;
			}

			@Override
			public void invalidResponse(int statusCode, String message) {
				fail();

			}
		}).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		assertEquals(1, count.get());

	}

	public void testGetWhenStaleCacheTimeUpCausesPullIfRepositoryExists() throws InterruptedException {
		checkCreateRepository(remoteGitServer, "a/b");
		put(remoteRoot, "a/b/c", v11);
		gitFacard.addAllAndCommit(remoteRoot, "a/b", "message");
		localGitServer.clone("a/b");

		put(remoteRoot, "a/b/c", v12);
		gitFacard.addAllAndCommit(remoteRoot, "a/b", "message");

		checkRespositoryGet("a/b", Maps.stringObjectMap("c", v11));
		checkRespositoryGet("a/b", Maps.stringObjectMap("c", v11));
		Thread.sleep(ServerConstants.staleCacheTimeForTests + 50);
		checkRespositoryGet("a/b", Maps.stringObjectMap("c", v12));
		checkRespositoryGet("a/b", Maps.stringObjectMap("c", v12));
		checkRespositoryGet("a/b", Maps.stringObjectMap("c", v12));

		put(remoteRoot, "a/b/c", v21);
		gitFacard.addAllAndCommit(remoteRoot, "a/b", "message");
		checkRespositoryGet("a/b", Maps.stringObjectMap("c", v12));
		checkRespositoryGet("a/b", Maps.stringObjectMap("c", v12));
		checkRespositoryGet("a/b", Maps.stringObjectMap("c", v12));
		Thread.sleep(ServerConstants.staleCacheTimeForTests + 50);
		checkRespositoryGet("a/b", Maps.stringObjectMap("c", v21));
	}

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

	private void checkRespositoryGet(String url, Map<String, Object> expected) {
		try {
			CheckRepositoryFacardCallback checkMatches = IRepositoryFacardCallback.Utils.checkMatches(ServerConstants.okStatusCode, expected);
			repositoryFacard.get(url, checkMatches).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
			checkMatches.assertCalled();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

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
		remoteAbc = new File(remoteRoot, Urls.compose("a/b/c", ServerConstants.dataFileName));
		localAbc = new File(localRoot, Urls.compose("a/b/c", ServerConstants.dataFileName));
		remoteGitServer = IGitServer.Utils.gitServer(remoteRoot, "not used");
		localGitServer = IGitServer.Utils.gitServer(localRoot, remoteRoot.getAbsolutePath());
		IProcessCall processCall = IProcessCall.Utils.softwareFmProcessCall(remoteGitServer, remoteRoot);
		server = ISoftwareFmServer.Utils.server(ServerConstants.testPort, 4, processCall, ICallback.Utils.<Throwable> memory());
		repositoryFacard = new GitRepositoryFacard(getHttpClient(), getServiceExecutor(), localGitServer, ServerConstants.staleCacheTimeForTests, ServerConstants.staleCacheTimeForTests);
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

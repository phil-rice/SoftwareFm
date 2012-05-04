package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Map;

import org.apache.log4j.Level;
import org.softwareFm.crowdsource.api.internal.Container;
import org.softwareFm.crowdsource.api.newGit.IRepoData;
import org.softwareFm.crowdsource.api.newGit.IRepoDataFactory;
import org.softwareFm.crowdsource.api.newGit.exceptions.FileDigestMismatchException;
import org.softwareFm.crowdsource.api.newGit.exceptions.MissingParameterException;
import org.softwareFm.crowdsource.api.newGit.exceptions.RepoSecurityException;
import org.softwareFm.crowdsource.api.newGit.exceptions.SecurityTokenMismatchException;
import org.softwareFm.crowdsource.api.newGit.facard.ISecurityTokenChecker;
import org.softwareFm.crowdsource.api.newGit.facard.ISecurityTokenMaker;
import org.softwareFm.crowdsource.api.newGit.facard.SecurityToken;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.server.RequestLineMock;
import org.softwareFm.crowdsource.constants.GitConstants;
import org.softwareFm.crowdsource.constants.SecurityConstants;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.tests.Tests;

@SuppressWarnings("unchecked")
public class RepoCommandsCallProcessorTest extends RepoTest {

	private final static String blankReply = "";
	private static String rl = "a/b/c/data.txt";

	private Container container;
	private RepoCommandsCallProcessor processor;
	private ISecurityTokenMaker tokenMaker;

	public void testFindRepo() {
		checkCommand(GitConstants.findRepoCommand, "a/b", "a/b");
		checkCommand(GitConstants.findRepoCommand, "a/b/c", "a/b");
		checkCommand(GitConstants.findRepoCommand, "a/c/d", "a/c/d");
		checkCommand(GitConstants.findRepoCommand, "a/c/d/e", "a/c/d");
		checkCommand(GitConstants.findRepoCommand, "q", "");
	}

	public void testAppend() {
		checkReadAll(remoteFacard, rl, v11);
		checkCommand(GitConstants.appendCommand, rl, blankReply, CommonConstants.dataParameterName, v12Json);
		checkReadAll(remoteFacard, rl, v11, v12);
		checkCommand(GitConstants.appendCommand, rl, blankReply, CommonConstants.dataParameterName, v21Json);
		checkReadAll(remoteFacard, rl, v11, v12, v21);
	}

	public void testAppendNeedsDataParameter() {
		checkNeedsParameter(GitConstants.appendCommand, MissingParameterException.class, "Missing critical parameters [data] in {", rl);
	}

	public void testAppendDataMustBeMap() {
		fail();
	}

	public void testDelete() {
		putFile(remoteFacard, rl, null, v11, v12, v21, v22, v31, v32);
		addAllAndCommit(remoteFacard, "a/b");

		checkReadAll(remoteFacard, rl, v11, v12, v21, v22, v31, v32);

		checkCommand(GitConstants.deleteCommand, rl, blankReply, CommonConstants.indexParameterName, "1");
		checkReadAll(remoteFacard, rl, v11, v21, v22, v31, v32);
	}

	public void testDeleteNeedsIndexParameter() {
		checkNeedsParameter(GitConstants.deleteCommand, MissingParameterException.class, "Missing critical parameters [index] in {", rl);
	}

	public void testChange() {
		putFile(remoteFacard, rl, null, v11, v12, v21, v22, v31, v32);
		addAllAndCommit(remoteFacard, "a/b");

		checkReadAll(remoteFacard, rl, v11, v12, v21, v22, v31, v32);

		checkCommand(GitConstants.changeCommand, rl, blankReply, CommonConstants.indexParameterName, "1", CommonConstants.dataParameterName, v11Json);
		checkReadAll(remoteFacard, rl, v11, v11, v21, v22, v31, v32);
	}

	public void testChangeNeedsDataAndIndexParameters() {
		checkNeedsParameter(GitConstants.changeCommand, MissingParameterException.class, "Missing critical parameters [index] in {", rl, CommonConstants.dataParameterName, "someData");
		checkNeedsParameter(GitConstants.changeCommand, MissingParameterException.class, "Missing critical parameters [data] in {", rl, CommonConstants.indexParameterName, "someIndex");
		checkNeedsParameter(GitConstants.changeCommand, MissingParameterException.class, "Missing critical parameters [data, index] in {", rl);
	}

	public void testChangeDataMustBeMap() {
				fail();
	}

	public void testCommandsObeySecurityToken() {
		checkCommandsObjectSecurityToken(GitConstants.appendCommand, rl, blankReply, CommonConstants.dataParameterName, v11Json);
		checkCommandsObjectSecurityToken(GitConstants.deleteCommand, rl, blankReply, CommonConstants.dataParameterName, v11Json);
	}

	private void checkCommand(String command, String uri, String expectedReply, String... extraParameters) {
		SecurityToken token = tokenMaker.generateToken(newRemoteRepo(), user1Data, rl);
		remoteRepoData.rollback();
		RequestLineMock requestLine = new RequestLineMock(CommonConstants.POST, uri);
		Map<String, Object> parameters = Maps.with(Maps.stringObjectMap(GitConstants.commandKey, command,//
				LoginConstants.softwareFmIdKey, user1Data.softwareFmId,//
				LoginConstants.emailKey, user1Data.email,//
				SecurityConstants.fileDigestKey, token.fileDigest,//
				SecurityConstants.securityTokenKey, token.token), extraParameters);
		IProcessResult result = processor.process(requestLine, parameters);
		IProcessResult.Utils.checkStringResult(result, expectedReply);
	}

	private void checkNeedsParameter(String command, Class<MissingParameterException> class1, String expectedMessagePrefix, String rl, String... extraParameters) {
		SecurityToken token = tokenMaker.generateToken(newRemoteRepo(), user1Data, rl);
		remoteRepoData.rollback();
		final RequestLineMock requestLine = new RequestLineMock(CommonConstants.POST, rl);
		final Map<String, Object> parameters = Maps.with(Maps.stringObjectMap(GitConstants.commandKey, command,//
				LoginConstants.softwareFmIdKey, user1Data.softwareFmId,//
				LoginConstants.emailKey, user1Data.email,//
				SecurityConstants.fileDigestKey, token.fileDigest,//
				SecurityConstants.securityTokenKey, token.token), extraParameters);
		MissingParameterException e = Tests.assertThrows(class1, new Runnable() {
			@Override
			public void run() {
				processor.process(requestLine, parameters);
			}
		});
		assertTrue(e.getMessage(), e.getMessage().startsWith(expectedMessagePrefix));
	}

	private void checkCommandsObjectSecurityToken(String command, String... extraParameters) {
		SecurityToken correctToken = tokenMaker.generateToken(newRemoteRepo(), user1Data, rl);
		remoteRepoData.rollback();

		SecurityToken differentDigest = new SecurityToken(correctToken.token, "WrongDigest");
		SecurityToken differentToken = new SecurityToken("WrongToken", correctToken.fileDigest);
		Map<String, Object> baseParameters = Maps.with(Maps.stringObjectMap(GitConstants.commandKey, command), extraParameters);//

		checkCommandsObjectSecurityToken(FileDigestMismatchException.class, differentDigest, command, baseParameters, LoginConstants.softwareFmIdKey, user1Data.softwareFmId, LoginConstants.emailKey, user1Data.email);
		checkCommandsObjectSecurityToken(SecurityTokenMismatchException.class, differentToken, command, baseParameters, LoginConstants.softwareFmIdKey, user1Data.softwareFmId, LoginConstants.emailKey, user1Data.email);
		checkCommandsObjectSecurityToken(MissingParameterException.class, differentToken, command, baseParameters, LoginConstants.softwareFmIdKey, user1Data.softwareFmId);
		checkCommandsObjectSecurityToken(MissingParameterException.class, differentToken, command, baseParameters, LoginConstants.emailKey, user1Data.email);
		checkCommandsObjectSecurityToken(MissingParameterException.class, differentToken, command, baseParameters);
	}

	private void checkCommandsObjectSecurityToken(Class<? extends RepoSecurityException> clazz, SecurityToken token, String command, Map<String, Object> baseParameters, Object... extraParameters) {
		final RequestLineMock requestLine = new RequestLineMock(CommonConstants.POST, rl);
		final Map<String, Object> parameters = Maps.with(Maps.with(baseParameters, extraParameters), SecurityConstants.fileDigestKey, token.fileDigest, SecurityConstants.securityTokenKey, token.token);
		Tests.assertThrows(clazz, new Runnable() {
			@Override
			public void run() {
				processor.process(requestLine, parameters);
			}
		});

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		initRepos(remoteFacard, "a/b", "a/c/d");
		putFile(remoteFacard, rl, null, v11);
		addAllAndCommit(remoteFacard, "a/b");

		container = new Container(transactionManager, null);
		container.register(IRepoData.class, IRepoDataFactory.Utils.simpleFactory(remoteFacard));
		processor = new RepoCommandsCallProcessor(container, userCryptoAccess, ISecurityTokenChecker.Utils.checker());
		tokenMaker = ISecurityTokenMaker.Utils.tokenMaker("Test");
	}

	static {
		ISecurityTokenMaker.logger.setLevel(Level.DEBUG);
	}

	public static void main(String[] args) {
		while (true)
			Tests.executeTest(RepoCommandsCallProcessorTest.class);
	}
}

package org.softwareFm.crowdsource.api.newGit.facard;

import java.text.MessageFormat;

import org.softwareFm.crowdsource.api.newGit.exceptions.FileDigestMismatchException;
import org.softwareFm.crowdsource.api.newGit.exceptions.SecurityTokenMismatchException;
import org.softwareFm.crowdsource.api.newGit.exceptions.SecurityTokenNotPresentException;
import org.softwareFm.crowdsource.api.newGit.internal.RepoTest;
import org.softwareFm.crowdsource.constants.SecurityMessages;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class ISecurityTokenCheckerTest extends RepoTest {
	private final static String rl1 = "a/b/c/data/txt";
	private final static String rl2 = "a/b/d/data/txt";
	private final ISecurityTokenMaker tokenMaker = ISecurityTokenMaker.Utils.tokenMaker("test");
	private final ISecurityTokenChecker tokenChecker = ISecurityTokenChecker.Utils.checker();

	public void testNoExceptionIfTokensMatch() {
		SecurityToken token1 = tokenMaker.generateToken(linkedRepoData, user1Data, rl1);
		SecurityToken token2 = tokenMaker.generateToken(linkedRepoData, user1Data, rl2);

		tokenChecker.validate(linkedRepoData, token1, user1Data, rl1);
		tokenChecker.validate(linkedRepoData, token2, user1Data, rl2);
	}

	public void testThrowsFileDigestMismatchExceptionIfFileDigestDontMatch() {
		SecurityToken token1 = tokenMaker.generateToken(linkedRepoData, user1Data, rl1);
		final SecurityToken token2 = new SecurityToken(token1.token, "not the correct digest");
		Tests.assertThrowsWithMessage("File Digest for a/b/c/data/txt has changed, so cannot check security token.\nExpected c8f7e1aa510d35c263347f6c7354859780121b10\nActual not the correct digest", FileDigestMismatchException.class, new Runnable() {
			@Override
			public void run() {
				tokenChecker.validate(linkedRepoData, token2, user1Data, rl1);
			}
		});
	}

	public void testThrowsSecurityTokenMismatchIfFileDigestsMatchButTokensDont() {
		SecurityToken token1 = tokenMaker.generateToken(linkedRepoData, user1Data, rl1);
		final SecurityToken token2 = new SecurityToken("WrongToken", token1.fileDigest);
		String expectedMessage = MessageFormat.format(SecurityMessages.securityMismatchException, user1Data, rl1, token1, token2);
		Tests.assertThrowsWithMessage(expectedMessage, SecurityTokenMismatchException.class, new Runnable() {
			@Override
			public void run() {
				tokenChecker.validate(linkedRepoData, token2, user1Data, rl1);
			}
		});
	}

	public void testWhenTokenHasNulls() {
		Tests.assertThrowsWithMessage("Token not present in SecurityToken [token=null, fileDigest=some token]. " + user1Data + " a/b/c/data/txt", SecurityTokenNotPresentException.class, new Runnable() {
			@Override
			public void run() {
				tokenChecker.validate(linkedRepoData, new SecurityToken(null, "some token"), user1Data, rl1);
			}
		});
		Tests.assertThrowsWithMessage("File Digest not present in SecurityToken [token=some token, fileDigest=null]. " + user1Data + " a/b/c/data/txt", SecurityTokenNotPresentException.class, new Runnable() {
			@Override
			public void run() {
				tokenChecker.validate(linkedRepoData, new SecurityToken("some token", null), user1Data, rl1);
			}
		});
		Tests.assertThrowsWithMessage("File Digest not present in SecurityToken [token=null, fileDigest=null]. " + user1Data + " a/b/c/data/txt", SecurityTokenNotPresentException.class, new Runnable() {
			@Override
			public void run() {
				tokenChecker.validate(linkedRepoData, new SecurityToken(null, null), user1Data, rl1);
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		initRepos(remoteFacard, "a/b");
		putFile(remoteFacard, rl1, null, v11, v12);
		putFile(remoteFacard, rl2, null, v11, v12);
		addAllAndCommit(remoteFacard, "a/b");

	}

}

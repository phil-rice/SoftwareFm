package org.softwareFm.crowdsource.api.newGit.facard;

import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.newGit.internal.RepoTest;

// The class is simple, and its hard to actually test with out just duplicating the implementation. This weakly checks some of the properties, and error conditions 
@SuppressWarnings("unchecked")
public class ISecurityTokenMakerTest extends RepoTest {

	private final static String rl1 = "a/b/c/data/txt";
	private final static String rl2 = "a/b/d/data/txt";
	private final static String fileCrypto = miscCrypto1;

	public void testGetSameTokenForSameData() {
		initRepos(remoteFacard, "a/b");
		putFile(remoteFacard, rl1, fileCrypto, v11, v12);
		putFile(remoteFacard, rl2, fileCrypto, v11, v12);
		addAllAndCommit(remoteFacard, "a/b");

		ISecurityTokenMaker tokenMaker = ISecurityTokenMaker.Utils.tokenMaker("test");
		SecurityToken token11a = tokenMaker.generateToken(linkedRepoData, user1Data, rl1);
		SecurityToken token11b = tokenMaker.generateToken(linkedRepoData, user1Data, rl1);

		SecurityToken token12a = tokenMaker.generateToken(linkedRepoData, user1Data, rl2);
		SecurityToken token12b = tokenMaker.generateToken(linkedRepoData, user1Data, rl2);

		SecurityToken token21a = tokenMaker.generateToken(linkedRepoData, user2Data, rl1);
		SecurityToken token21b = tokenMaker.generateToken(linkedRepoData, user2Data, rl1);

		SecurityToken token22a = tokenMaker.generateToken(linkedRepoData, user2Data, rl2);
		SecurityToken token22b = tokenMaker.generateToken(linkedRepoData, user2Data, rl2);

		assertEquals(token11a, token11b);
		assertEquals(token12a, token12b);
		assertEquals(token21a, token21b);
		assertEquals(token22a, token22b);
	}

	public void testDifferentUrlsMakesDifferentTokens() {
		initRepos(remoteFacard, "a/b");
		putFile(remoteFacard, rl1, null, v11, v12);
		putFile(remoteFacard, rl2, null, v11, v12);
		addAllAndCommit(remoteFacard, "a/b");

		ISecurityTokenMaker tokenMaker = ISecurityTokenMaker.Utils.tokenMaker("test");
		SecurityToken token1 = tokenMaker.generateToken(linkedRepoData, user1Data, rl1);
		SecurityToken token2 = tokenMaker.generateToken(linkedRepoData, user1Data, rl2);
		assertFalse(token1.equals(token2));
	}

	public void testDifferentFileContentsMakesDifferentTokens() {
		ISecurityTokenMaker tokenMaker = ISecurityTokenMaker.Utils.tokenMaker("test");
		initRepos(remoteFacard, "a/b");

		putFile(remoteFacard, rl1, null, v11, v12);
		addAllAndCommit(remoteFacard, "a/b");
		SecurityToken token1 = tokenMaker.generateToken(linkedRepoData, user1Data, rl1);
		putFile(remoteFacard, rl1, null, v11, v21);
		addAllAndCommit(remoteFacard, "a/b");
		SecurityToken token2 = tokenMaker.generateToken(newLinkedRepo(), user1Data, rl1);
		assertFalse(token1.equals(token2));
	}

	public void testDifferentUsersMakesDifferentTokens() {
		initRepos(remoteFacard, "a/b");
		putFile(remoteFacard, rl1, null, v11, v12);
		addAllAndCommit(remoteFacard, "a/b");

		ISecurityTokenMaker tokenMaker = ISecurityTokenMaker.Utils.tokenMaker("test");
		SecurityToken token1 = tokenMaker.generateToken(linkedRepoData, user1Data, rl1);
		SecurityToken token2 = tokenMaker.generateToken(linkedRepoData, user2Data, rl1);
		SecurityToken token3 = tokenMaker.generateToken(linkedRepoData, new UserData(null, null, null), rl1);
		assertFalse(token1.equals(token2));
		assertFalse(token1.equals(token3));
	}

	public void testDifferentUserIdsMakesDifferentTokens() {
		initRepos(remoteFacard, "a/b");
		putFile(remoteFacard, rl1, null, v11, v12);
		addAllAndCommit(remoteFacard, "a/b");

		ISecurityTokenMaker tokenMaker = ISecurityTokenMaker.Utils.tokenMaker("test");
		SecurityToken token1 = tokenMaker.generateToken(linkedRepoData, new UserData(user1Data.email, "id1", user1Data.crypto), rl1);
		SecurityToken token2 = tokenMaker.generateToken(linkedRepoData, new UserData(user1Data.email, "id2", user1Data.crypto), rl1);
		assertFalse(token1.equals(token2));
	}

	public void testDifferentUserCryptosMakesDifferentTokens() {
		initRepos(remoteFacard, "a/b");
		putFile(remoteFacard, rl1, null, v11, v12);
		addAllAndCommit(remoteFacard, "a/b");

		ISecurityTokenMaker tokenMaker = ISecurityTokenMaker.Utils.tokenMaker("test");
		SecurityToken token1 = tokenMaker.generateToken(linkedRepoData, new UserData(user1Data.email, user1Data.softwareFmId, miscCrypto1), rl1);
		SecurityToken token2 = tokenMaker.generateToken(linkedRepoData, new UserData(user1Data.email, user1Data.softwareFmId, miscCrypto2), rl1);
		SecurityToken token3 = tokenMaker.generateToken(linkedRepoData, new UserData(null, null, null), rl1);
		assertFalse(token1.equals(token2));
		assertFalse(token1.equals(token3));
	}

	public void testProducesTokenWhenNotLoggedIn() {
		initRepos(remoteFacard, "a/b");
		putFile(remoteFacard, rl1, null, v11, v12);
		addAllAndCommit(remoteFacard, "a/b");

		ISecurityTokenMaker tokenMaker = ISecurityTokenMaker.Utils.tokenMaker("test");
		SecurityToken token1 = tokenMaker.generateToken(linkedRepoData, new UserData(null, null, null), rl1);
		SecurityToken token2 = tokenMaker.generateToken(linkedRepoData, new UserData(null, null, null), rl1);
		assertEquals(token1, token2);
	}
}

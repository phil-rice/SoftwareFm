package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Collections;
import java.util.List;

import org.softwareFm.crowdsource.api.newGit.ISingleSource;

public class UserSourceTest extends AbstractSourceTypeTest {
	public void testReturnsEncryptedSourcePrefixedWithUserUrl() {
		UserSourceType userSourceType = new UserSourceType(userUrlGenerator);
		List<ISingleSource> actual = userSourceType.makeSourcesFor(linkedRepoData, "rl", "file", userId1, userCrypto1, "cryptoKey");
		assertEquals(Collections.singletonList(new EncryptedSingleSource("data/users/uI/d1/uId1/rl/file", miscCrypto1)), actual);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		initRepos(remoteFacard, "data/users");
		createArbitaryFileForUser(remoteFacard, userId1, userCrypto1, "cryptoKey", miscCrypto1);
		addAllAndCommit(remoteFacard, "data/users");
	}
}
 
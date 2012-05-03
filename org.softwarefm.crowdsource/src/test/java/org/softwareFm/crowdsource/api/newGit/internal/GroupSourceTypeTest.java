package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.constants.CommentConstants;

//unlike the other source type tests, this doesnt check for an arbitary 'cryptoKey', it checks for comments. To use arbitary 'cryptoKey' like the others would require a lot of data making
public class GroupSourceTypeTest extends AbstractSourceTypeTest {

	private final EncryptedSingleSource group1Source = new EncryptedSingleSource("data/groups/gI/d1/gId1/rl/file", groupCommentCrypto1);
	private final EncryptedSingleSource group2Source = new EncryptedSingleSource("data/groups/gI/d2/gId2/rl/file", groupCommentCrypto2);
	private final EncryptedSingleSource group3Source = new EncryptedSingleSource("data/groups/gI/d3/gId3/rl/file", groupCommentCrypto3);

	public void testReturnsEncryptedSourcePrefixedWithGroupUrl() {
		GroupSourceType userSourceType = new GroupSourceType(userUrlGenerator, groupUrlGenerator);
		List<ISingleSource> actual = userSourceType.makeSourcesFor(repoData, "rl", "file", userId1, userCrypto1, CommentConstants.commentCryptoKey);
		assertEquals(Arrays.asList(group1Source, group2Source, group3Source), actual);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		initRepos(remoteFacard, "data/users", "data/groups");
		createFileForUser1(remoteFacard);
		createMembershipFileForUser(remoteFacard, userId1, userMembershipCrypto1, membershipMapForGroup1Admin, membershipMapForGroup2Member, membershipMapForGroup3Invited);
		addAllAndCommit(remoteFacard, "data/users", "data/groups");
	}
}

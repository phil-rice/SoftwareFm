package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Map;

import org.softwareFm.crowdsource.api.git.GitTest;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;

abstract public class RepoTest extends GitTest {

	protected static final IUrlGenerator userUrlGenerator = LoginConstants.userGenerator("data");
	protected static final IUrlGenerator groupUrlGenerator = GroupConstants.groupsGenerator("data");

	protected static final String userId1 = "uId1";
	protected static final String userId2 = "uId2";
	protected static final String userId3 = "uId3";

	protected static final String userCrypto1 = Crypto.makeKey();
	protected static final String userCrypto2 = Crypto.makeKey();
	protected static final String userCrypto3 = Crypto.makeKey();

	protected static final String userMembershipCrypto1 = Crypto.makeKey();
	protected static final String userMembershipCrypto2 = Crypto.makeKey();
	protected static final String userMembershipCrypto3 = Crypto.makeKey();

	protected static final String userCommentCrypto1 = Crypto.makeKey();
	protected static final String userCommentCrypto2 = Crypto.makeKey();
	protected static final String userCommentCrypto3 = Crypto.makeKey();

	protected static final String groupId1 = "gId1";
	protected static final String groupId2 = "gId2";
	protected static final String groupId3 = "gId3";

	protected static final String groupCrypto1 = Crypto.makeKey();
	protected static final String groupCrypto2 = Crypto.makeKey();
	protected static final String groupCrypto3 = Crypto.makeKey();

	protected static final String groupCommentCrypto1 = Crypto.makeKey();
	protected static final String groupCommentCrypto2 = Crypto.makeKey();
	protected static final String groupCommentCrypto3 = Crypto.makeKey();

	protected static final String groupMembershipCrypto1 = Crypto.makeKey();
	protected static final String groupMembershipCrypto2 = Crypto.makeKey();
	protected static final String groupMembershipCrypto3 = Crypto.makeKey();
	
	protected static final String miscCrypto1 = Crypto.makeKey();
	protected static final String miscCrypto2 = Crypto.makeKey();
	protected static final String miscCrypto3 = Crypto.makeKey();

	protected static final Map<String, Object> membershipMapForGroup1Admin = makeMembershipMap(groupId1, groupMembershipCrypto1, groupCommentCrypto1, GroupConstants.adminStatus);
	protected static final Map<String, Object> membershipMapForGroup1Member = makeMembershipMap(groupId1,groupMembershipCrypto1,  groupCommentCrypto1, GroupConstants.memberStatus);
	protected static final Map<String, Object> membershipMapForGroup1Invited = makeMembershipMap(groupId1, groupMembershipCrypto1, groupCommentCrypto1, GroupConstants.invitedStatus);

	protected static final Map<String, Object> membershipMapForGroup2Admin = makeMembershipMap(groupId2, groupMembershipCrypto2, groupCommentCrypto2, GroupConstants.adminStatus);
	protected static final Map<String, Object> membershipMapForGroup2Member = makeMembershipMap(groupId2, groupMembershipCrypto2, groupCommentCrypto2, GroupConstants.memberStatus);
	protected static final Map<String, Object> membershipMapForGroup2Invited = makeMembershipMap(groupId2, groupMembershipCrypto2, groupCommentCrypto2, GroupConstants.invitedStatus);

	protected static final Map<String, Object> membershipMapForGroup3Admin = makeMembershipMap(groupId3, groupMembershipCrypto3, groupCommentCrypto3, GroupConstants.adminStatus);
	protected static final Map<String, Object> membershipMapForGroup3Member = makeMembershipMap(groupId3, groupMembershipCrypto3, groupCommentCrypto3, GroupConstants.memberStatus);
	protected static final Map<String, Object> membershipMapForGroup3Invited = makeMembershipMap(groupId3, groupMembershipCrypto3, groupCommentCrypto3, GroupConstants.invitedStatus);

	protected IGitFacard gitFacard;
	protected RepoData repoData;

	protected void initRepos(IGitFacard gitFacard, String... rls) {
		for (String rl : rls)
			gitFacard.init(rl);
	}

	protected void commitRepos(IGitFacard gitFacard, String... rls) {
		for (String rl : rls)
			gitFacard.commit(gitFacard.addAll(rl), "someCommitMessage");
	}

	/** Note doesnt add or commit it, as that is best done only once. You need to have inited the repository before this will work */
	protected void createArbitaryFileForUser(IGitFacard gitFacard, String userId, String userCrypto, Object... namesAndAttributes) {
		Map<String, Object> map = Maps.<String, Object> makeMapWithoutNullValues(namesAndAttributes);
		String userProperiesAsEncryptedString = Crypto.aesEncrypt(userCrypto, Json.toString(map));
		String userRl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, userId));
		String userDataRl = Urls.compose(userRl, CommonConstants.dataFileName);
		gitFacard.putFileReturningRepoRl(userDataRl, userProperiesAsEncryptedString);
	}

	/** Note doesnt add or commit it, as that is best done only once. You need to have inited the repository before this will work */
	protected void createFileForUser(IGitFacard gitFacard, String userId, String userCrypto, String membershipCrypto, String commentCrypto) {
		createArbitaryFileForUser(gitFacard, userId, userCrypto, CommentConstants.commentCryptoKey, commentCrypto, GroupConstants.membershipCryptoKey, membershipCrypto);
	}

	protected void createFileForUser1(IGitFacard gitFacard) {
		createFileForUser(gitFacard, userId1, userCrypto1, userMembershipCrypto1, userCommentCrypto1);
	}

	protected void createMembershipFileForUser(IGitFacard gitFacard, String userId, String membershipCrypto, Map<String, Object>... membershipData) {
		String text = Strings.join(Iterables.mapValues(Json.toStringAndEncryptFn(membershipCrypto), membershipData), "\n");
		String userRl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, userId));
		String membershipRl = Urls.compose(userRl, GroupConstants.membershipFileName);
		gitFacard.putFileReturningRepoRl(membershipRl, text);
	}

	static protected Map<String, Object> makeMembershipMap(String groupId, String membershipCrypto, String commentCrypto, String status) {
		return Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.membershipCryptoKey, membershipCrypto, CommentConstants.commentCryptoKey, commentCrypto, GroupConstants.membershipStatusKey, status);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gitFacard = new GitFacard(remoteRoot);
		repoData = new RepoData(gitFacard, "someCommitMessage");
	}
}

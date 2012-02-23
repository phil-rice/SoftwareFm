package org.softwareFm.eclipse.comments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUser;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.user.IUserMembership;
import org.softwareFm.eclipse.user.IUserMembershipReader;

public abstract class AbstractCommentsReaderTest extends GitTest {
	abstract protected IUser makeUser();

	abstract protected IUserMembership makeUserMembership(IUser user, String softwareFmId, String crypto);

	abstract protected IGroups makeGroups();

	abstract protected ICommentsReader makeReader(IUser user, IUserMembershipReader userMembershipReader, IGroupsReader groupsReader, String softwareFmId, String userCrypto, String commentsCrypto);

	protected final long time1 = 1000l;
	protected final long time2 = 2000l;
	protected final Map<String, Object> comment1 = Maps.stringObjectMap(CommentConstants.timeKey, time1, LoginConstants.softwareFmIdKey, "sfm1", CommentConstants.creatorKey, "moniker", CommentConstants.textKey, "text1");
	protected final Map<String, Object> comment2 = Maps.stringObjectMap(CommentConstants.timeKey, time2, LoginConstants.softwareFmIdKey, "sfm1", CommentConstants.creatorKey, "moniker", CommentConstants.textKey, "text2");
	protected String userCrypto;
	protected String userCommentsCrypto;
	protected String softwareFmId;
	protected IUser user;
	protected ICommentsReader reader;
	protected String groupsCrypto;
	protected IUserMembership userMembership;
	protected IGroups groups;

	@SuppressWarnings("unchecked")
	public void testGroupComments() {
		remoteOperations.init("a");
		user.setUserProperty(softwareFmId, userCrypto, GroupConstants.groupCryptoKey, groupsCrypto);
		user.setUserProperty(softwareFmId, userCrypto, GroupConstants.membershipCryptoKey, Crypto.makeKey());

		Map<String, String> groupIdToCrypto = setUpGroups("groupId1", "groupId2", "groupId3");
		for (String groupId : groupIdToCrypto.keySet()) {
			String groupCrypto = groupIdToCrypto.get(groupId);
			groups.setGroupProperty(groupId, groupCrypto, GroupConstants.groupNameKey, groupId + "Name");
			String groupCommentCrypto = groups.getGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey);

			IFileDescription fd = IFileDescription.Utils.encrypted("a/b", groupId + "." + CommentConstants.commentExtension, groupCommentCrypto);
			remoteOperations.append(fd, Maps.with(comment1, "a", groupId));
			remoteOperations.append(fd, comment2);
			remoteOperations.addAllAndCommit("a", "testGlobal");
		}
		String sourcekey = CommentConstants.sourceKey;
		assertEquals(Arrays.asList(//
				Maps.with(comment1, "a", "groupId1", sourcekey, "groupId1Name"), Maps.with(comment2, sourcekey, "groupId1Name"),//
				Maps.with(comment1, "a", "groupId2", sourcekey, "groupId2Name"), Maps.with(comment2, sourcekey, "groupId2Name"),//
				Maps.with(comment1, "a", "groupId3", sourcekey, "groupId3Name"), Maps.with(comment2, sourcekey, "groupId3Name")), reader.groupComments("a/b", softwareFmId, userCrypto));
	}

	@SuppressWarnings("unchecked")
	public void testGlobal() {
		IFileDescription fd = IFileDescription.Utils.plain("a/b", CommentConstants.globalCommentsFile);
		remoteOperations.init("a");
		remoteOperations.append(fd, comment1);
		remoteOperations.append(fd, comment2);
		remoteOperations.addAllAndCommit("a", "testGlobal");
		assertEquals(addSource("asd", comment1, comment2), reader.globalComments("a/b", "asd"));
	}

	@SuppressWarnings("unchecked")
	public void testUserComments() {
		user.setUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey, userCommentsCrypto);
		IFileDescription fd = IFileDescription.Utils.encrypted("a/b", softwareFmId + "." + CommentConstants.commentExtension, userCommentsCrypto);
		remoteOperations.init("a");
		remoteOperations.append(fd, comment1);
		remoteOperations.append(fd, comment2);
		remoteOperations.addAllAndCommit("a", "testGlobal");
		assertEquals(addSource("asd", comment1, comment2), reader.myComments("a/b", softwareFmId, userCrypto, "asd"));
	}

	public void testUserCommentsWhenNoCommentsHaveBeenMade() {
		user.setUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey, userCommentsCrypto);
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/z"), v11);
		remoteOperations.addAllAndCommit("a", "testUserCommentsWhenNoCommentsHaveBeenMade");// needed to ensure that the repository is in a good state
		assertEquals(Collections.emptyList(), reader.myComments("a/b", softwareFmId, userCrypto, "asd"));
	}

	protected Map<String, String> setUpGroups(String... groupIds) {
		Map<String, String> groupIdToCrypto = Maps.newMap();
		for (String groupId : groupIds) {
			String groupCrypto = Crypto.makeKey();
			groups.setGroupProperty(groupId, groupCrypto, GroupConstants.groupNameKey, groupId + "Name");
			String groupCommentCrypto = Crypto.makeKey();
			groupIdToCrypto.put(groupId, groupCrypto);
			groups.setGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey, groupCommentCrypto);
			userMembership.addMembership(softwareFmId, userCrypto, groupId, groupCrypto, GroupConstants.invitedStatus);
		}
		return groupIdToCrypto;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userCrypto = Crypto.makeKey();
		userCommentsCrypto = Crypto.makeKey();
		groupsCrypto = Crypto.makeKey();
		softwareFmId = "sfm1";
		user = makeUser();
		user.setUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey, userCommentsCrypto);
		userMembership = makeUserMembership(user, softwareFmId, userCrypto);
		groups = makeGroups();
		reader = makeReader(user, userMembership, groups, softwareFmId, userCrypto, userCommentsCrypto);

	}

	protected List<Map<String, Object>> addSource(String source, Map<String, Object>... rawExpected) {
		List<Map<String, Object>> expected = Lists.map(Arrays.asList(rawExpected), Maps.<String, Object> withFn(CommentConstants.sourceKey, source));
		return expected;
	}

}

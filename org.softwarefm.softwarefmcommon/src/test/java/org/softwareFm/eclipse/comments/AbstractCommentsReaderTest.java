package org.softwareFm.eclipse.comments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.GroupConstants;
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

	abstract protected ICommentsReader makeReader(IUser user, IUserMembershipReader userMembershipReader, String softwareFmId, String userCrypto, String commentsCrypto);

	protected final Map<String, Object> comment1 = Maps.stringObjectMap(CommentConstants.timeKey, 1l, CommentConstants.creatorKey, "sfm1", CommentConstants.textKey, "text1");
	protected final Map<String, Object> comment2 = Maps.stringObjectMap(CommentConstants.timeKey, 2l, CommentConstants.creatorKey, "sfm2", CommentConstants.textKey, "text2");
	private String userCrypto;
	private String commentsCrypto;
	private String softwareFmId;
	private IUser user;
	private ICommentsReader reader;
	private String groupsCrypto;
	private IUserMembership userMembership;

	@SuppressWarnings("unchecked")
	public void testGlobal() {
		IFileDescription fd = IFileDescription.Utils.plain("a/b", CommentConstants.globalCommentsFile);
		remoteOperations.init("a");
		remoteOperations.append(fd, comment1);
		remoteOperations.append(fd, comment2);
		remoteOperations.addAllAndCommit("a", "testGlobal");
		assertEquals(Arrays.asList(comment1, comment2), reader.globalComments("a/b"));
	}

	@SuppressWarnings("unchecked")
	public void testUserComments() {
		user.setUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey, commentsCrypto);
		IFileDescription fd = IFileDescription.Utils.encrypted("a/b", softwareFmId + "." + CommentConstants.commentExtension, commentsCrypto);
		remoteOperations.init("a");
		remoteOperations.append(fd, comment1);
		remoteOperations.append(fd, comment2);
		remoteOperations.addAllAndCommit("a", "testGlobal");
		assertEquals(Arrays.asList(comment1, comment2), reader.myComments("a/b", softwareFmId));
	}

	public void testUserCommentsWhenNoCommentsHaveBeenMade() {
		user.setUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey, commentsCrypto);
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/z"), v11);
		remoteOperations.addAllAndCommit("a", "testUserCommentsWhenNoCommentsHaveBeenMade");//needed to ensure that the repository is in a good state  
		assertEquals(Collections.emptyList(), reader.myComments("a/b", softwareFmId));
	}

	@SuppressWarnings("unchecked")
	public void testGroupComments() {
		user.setUserProperty(softwareFmId, userCrypto, GroupConstants.groupCryptoKey, groupsCrypto);
		String membershipCrypto = Crypto.makeKey();
		user.setUserProperty(softwareFmId, userCrypto, GroupConstants.membershipCryptoKey, membershipCrypto);
		assertEquals(groupsCrypto, user.getUserProperty(softwareFmId, userCrypto, GroupConstants.groupCryptoKey));
		assertEquals(membershipCrypto, user.getUserProperty(softwareFmId, userCrypto, GroupConstants.membershipCryptoKey));
		IGroups groups = makeGroups();
		List<String> groupIds = Arrays.asList("groupId1", "groupId2", "groupId3");
		for (String groupId : groupIds) {
			String groupCrypto = Crypto.makeKey();
			groups.setGroupProperty(groupId, groupCrypto, GroupConstants.groupNameKey, groupId + "Name");
			userMembership.addMembership(softwareFmId, groupId, groupCrypto, GroupConstants.invitedStatus);
			IFileDescription fd = IFileDescription.Utils.encrypted("a/b", groupId + "." + CommentConstants.commentExtension, groupCrypto);
			remoteOperations.init("a");
			remoteOperations.append(fd, Maps.with(comment1, "a", groupId));
			remoteOperations.append(fd, comment2);
			remoteOperations.addAllAndCommit("a", "testGlobal");
		}
		assertEquals(Arrays.asList(//
				Maps.with(comment1, "a", "groupId1"), comment2,//
				Maps.with(comment1, "a", "groupId2"), comment2,//
				Maps.with(comment1, "a", "groupId3"), comment2), reader.groupComments("a/b", softwareFmId));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userCrypto = Crypto.makeKey();
		commentsCrypto = Crypto.makeKey();
		groupsCrypto = Crypto.makeKey();
		softwareFmId = "sfm1";
		user = makeUser();
		userMembership = makeUserMembership(user, softwareFmId, userCrypto);
		reader = makeReader(user, userMembership, softwareFmId, userCrypto, commentsCrypto);

	}

}

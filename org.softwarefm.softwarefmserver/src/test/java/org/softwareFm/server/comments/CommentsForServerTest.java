package org.softwareFm.server.comments;

import java.util.Collections;
import java.util.concurrent.Callable;

import org.softwareFm.common.IGroups;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.comments.AbstractCommentsReaderTest;
import org.softwareFm.eclipse.comments.ICommentsReader;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IUserMembership;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.softwareFmServer.GroupsForServer;
import org.softwareFm.softwareFmServer.UserMembershipForServer;

public class CommentsForServerTest extends AbstractCommentsReaderTest {
	IUrlGenerator userUrlGenerator = LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix);

	@Override
	protected ICommentsReader makeReader(IUser user, IUserMembershipReader userMembershipReader, String softwareFmId, String usersCrypto, String commentsCrypto) {
		return new CommentsForServer(remoteOperations, user, userMembershipReader, Functions.<String, String> map(softwareFmId, usersCrypto));
	}

	@Override
	protected IUser makeUser() {
		return ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, userUrlGenerator, Strings.firstNSegments(3), Collections.<String, Callable<Object>> emptyMap());
	}

	@Override
	protected IUserMembership makeUserMembership(IUser user, String softwareFmId, String crypto) {
		return new UserMembershipForServer(userUrlGenerator, remoteOperations, user, Functions.mapFromKey(LoginConstants.softwareFmIdKey, softwareFmId, crypto), Strings.firstNSegments(3));
	}

	@Override
	protected IGroups makeGroups() {
		return new GroupsForServer(GroupConstants.groupsGenerator(SoftwareFmConstants.urlPrefix), remoteOperations, Strings.firstNSegments(3));
	}

}

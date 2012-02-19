package org.softwareFm.common.comments;

import java.util.Collections;
import java.util.concurrent.Callable;

import org.softwareFm.common.IGroups;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
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
import org.softwareFm.swt.comments.CommentsReaderLocal;

public class CommentsReaderLocalTest extends AbstractCommentsReaderTest {
	IUrlGenerator userUrlGenerator = LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix);

	@Override
	protected ICommentsReader makeReader(IUser user, IUserMembershipReader userMembershipReader, IGroupsReader groupsReader, String softwareFmId, String userCrypto, String commentsCrypto) {
		return new CommentsReaderLocal(gitLocal, user, userMembershipReader, groupsReader, softwareFmId, commentsCrypto);
	}

	@Override
	protected IUser makeUser() {
		return ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, userUrlGenerator, Strings.firstNSegments(3), Collections.<String, Callable<Object>> emptyMap());
	}

	@Override
	protected IUserMembership makeUserMembership(IUser user, String softwareFmId, String crypto) {
		return new UserMembershipForServer(userUrlGenerator, user, remoteOperations, Strings.firstNSegments(3));
	}

	@Override
	protected IGroups makeGroups() {
		return new GroupsForServer(GroupConstants.groupsGenerator(SoftwareFmConstants.urlPrefix), remoteOperations, Strings.firstNSegments(3));
	}

}

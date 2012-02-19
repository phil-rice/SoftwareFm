package org.softwareFm.swt.comments;

import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.eclipse.comments.AbstractCommentsReader;
import org.softwareFm.eclipse.user.IUserMembershipReader;

public class CommentsReaderLocal extends AbstractCommentsReader {


	public CommentsReaderLocal(IGitLocal gitLocal, IUserReader userReader, IUserMembershipReader userMembershipReader, IGroupsReader groupsReader, String softwareFmId, String commentCrypto) {
		super(gitLocal, userReader, userMembershipReader, groupsReader);
	}


}

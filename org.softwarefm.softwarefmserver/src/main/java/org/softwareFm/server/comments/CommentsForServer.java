package org.softwareFm.server.comments;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.eclipse.comments.AbstractCommentsReader;
import org.softwareFm.eclipse.comments.ICommentDefn;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.user.IUserMembershipReader;

public class CommentsForServer extends AbstractCommentsReader implements IComments {


	private final IGitOperations gitOperations;
	private final Callable<Long> timeGetter;
	private final IUser user;

	public CommentsForServer(IGitOperations gitOperations, IUser user, IUserMembershipReader userMembershipReader, IGroupsReader groupsReader, Callable<Long> timeGetter) {
		super(gitOperations, user, userMembershipReader, groupsReader);
		this.gitOperations = gitOperations;
		this.user = user;
		this.timeGetter = timeGetter;
		
	}


	@Override
	public void add(String softwareFmId, String userCrypto, ICommentDefn defn, String text) {
		Map<String, Object> data = makeData(softwareFmId, userCrypto, text);
		IFileDescription fileDescription = defn.fileDescription();
		gitOperations.append(fileDescription, data);
	}

	private Map<String, Object> makeData(String softwareFmId, String userCrypto, String text) {
		return Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, //
				CommentConstants.textKey, text, //
				CommentConstants.creatorKey, user.getUserProperty(softwareFmId, userCrypto, LoginConstants.monikerKey),//
				CommentConstants.timeKey, Callables.call(timeGetter));
	}


}

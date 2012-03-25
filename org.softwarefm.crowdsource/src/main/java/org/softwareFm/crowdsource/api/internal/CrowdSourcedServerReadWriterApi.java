package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.git.IGitWriter;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.comments.internal.CommentsForServer;
import org.softwareFm.crowdsource.git.internal.GitOperations;
import org.softwareFm.crowdsource.git.internal.GitWriterForServer;
import org.softwareFm.crowdsource.user.internal.GroupsForServer;
import org.softwareFm.crowdsource.user.internal.ServerUser;
import org.softwareFm.crowdsource.user.internal.UserMembershipForServer;

public class CrowdSourcedServerReadWriterApi extends AbstractCrowdSourceReadWriterApi {

	private final IGitOperations gitOperations;
	private final ServerUser user;
	private final GroupsForServer groups;
	private final UserMembershipForServer userMembership;
	private final CommentsForServer comments;

	public CrowdSourcedServerReadWriterApi(ServerConfig serverConfig) {
		this.gitOperations = new GitOperations(serverConfig.root);
		this.user = new ServerUser(gitOperations, serverConfig.userUrlGenerator, serverConfig.userRepoDefnFn, serverConfig.defaultUserValues);
		this.groups = new GroupsForServer(this, serverConfig.groupUrlGenerator, serverConfig.groupRepoDefnFn, serverConfig.defaultGroupProperties);
		this.userMembership = new UserMembershipForServer(this, serverConfig.userUrlGenerator, serverConfig.userRepoDefnFn);
		this.comments = new CommentsForServer(this, serverConfig.timeGetter);
		registerReadWriter(IComments.class, comments);
		registerReader(ICommentsReader.class, comments);

		registerReadWriter(IGroups.class, groups);
		registerReader(IGroupsReader.class, groups);

		registerReadWriter(IUserMembership.class, userMembership);
		registerReader(IUserMembershipReader.class, userMembership);

		registerReadWriter(IUser.class, user);
		registerReader(IUserReader.class, user);

		registerReader(IGitReader.class, gitOperations);
		registerReaderAndWriter(IGitWriter.class, new GitWriterForServer(gitOperations));
	}

	@Override
	public IGitOperations gitOperations() {
		return gitOperations;
	}
}

/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.ServerConfig;
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
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;

public class ContainerForServer extends Container {

	private final ServerUser user;
	private final GroupsForServer groups;
	private final UserMembershipForServer userMembership;
	private final CommentsForServer comments;

	public ContainerForServer(ServerConfig serverConfig, ITransactionManager transactionManager) {
		super(transactionManager, new GitOperations(serverConfig.root));
		this.user = new ServerUser(gitOperations(), serverConfig.userUrlGenerator, serverConfig.userRepoDefnFn, serverConfig.defaultUserValues);
		this.groups = new GroupsForServer(this, serverConfig.groupUrlGenerator, serverConfig.groupRepoDefnFn, serverConfig.defaultGroupProperties);
		this.userMembership = new UserMembershipForServer(this, serverConfig.userUrlGenerator, serverConfig.userRepoDefnFn);
		this.comments = new CommentsForServer(this, serverConfig.timeGetter);
		register(IComments.class, comments);
		register(ICommentsReader.class, comments);

		register(IGroups.class, groups);
		register(IGroupsReader.class, groups);

		register(IUserMembership.class, userMembership);
		register(IUserMembershipReader.class, userMembership);

		register(IUser.class, user);
		register(IUserReader.class, user);

		register(IGitReader.class, gitOperations());
		register(IGitWriter.class, new GitWriterForServer(gitOperations()));
	}

}
package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback.EnsureSameParameter;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback3;

public class CrowdSourceServerReadWriteApi extends AbstractCrowdReadWriterApiTest {
	public void testGetSameReadWriters() {
		final IUserAndGroupsContainer container = getApi().makeUserAndGroupsContainer();

		final EnsureSameParameter<IUser> usersCallback = ICallback.Utils.ensureSameParameter();
		final EnsureSameParameter<IGroups> groupsCallback = ICallback.Utils.ensureSameParameter();
		final ICallback2<IGroups, IUserMembership> userMembershipCallback = ICallback2.Utils.ensureSameParameters();

		container.modifyGroups(groupsCallback);
		container.modify(IGroups.class, groupsCallback);
		container.modifyGroups(groupsCallback);
		assertEquals(3, groupsCallback.count.get());

		container.modifyUser(usersCallback);
		container.modify(IUser.class, usersCallback);
		container.modifyUser(usersCallback);
		assertEquals(3, usersCallback.count.get());

		container.modifyUserMembership(userMembershipCallback);
		container.modifyUserMembership(userMembershipCallback);
		container.modify(IUserMembership.class, new ICallback<IUserMembership>() {
			@Override
			public void process(final IUserMembership userMembership) throws Exception {
				container.modify(IGroups.class, new ICallback<IGroups>() {
					@Override
					public void process(IGroups groups) throws Exception {
						userMembershipCallback.process(groups, userMembership);
					}
				});
			}
		});

		container.modify(IGroups.class, IUser.class, new ICallback2<IGroups, IUser>() {
			@Override
			public void process(IGroups groups, IUser user) throws Exception {
				groupsCallback.process(groups);
				usersCallback.process(user);
			}
		});

		container.modify(IUserMembership.class, IUser.class, IGroups.class, new ICallback3<IUserMembership, IUser, IGroups>() {
			@Override
			public void process(IUserMembership userMembership, IUser user, IGroups groups) throws Exception {
				userMembershipCallback.process(groups, userMembership);
				usersCallback.process(user);
				groupsCallback.process(groups);
			}
		});

		EnsureSameParameter<IComments> commentsCallback = ICallback.Utils.ensureSameParameter();
		container.modifyComments(commentsCallback);
		container.modify(IComments.class, commentsCallback);
		container.modifyComments(commentsCallback);
		assertEquals(3, commentsCallback.count.get());
	}

	@Override
	protected ICrowdSourcedApi getApi() {
		return getServerApi();
	}

}

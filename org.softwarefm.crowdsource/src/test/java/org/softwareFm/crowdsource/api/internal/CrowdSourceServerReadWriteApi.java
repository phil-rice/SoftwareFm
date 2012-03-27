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

		container.accessGroups(groupsCallback).get();
		container.access(IGroups.class, groupsCallback).get();
		container.accessGroups(groupsCallback).get();
		assertEquals(3, groupsCallback.count.get());

		container.accessUser(usersCallback).get();
		container.access(IUser.class, usersCallback).get();
		container.accessUser(usersCallback).get();
		assertEquals(3, usersCallback.count.get());

		container.accessUserMembership(userMembershipCallback).get();
		container.accessUserMembership(userMembershipCallback).get();
		container.access(IUserMembership.class, new ICallback<IUserMembership>() {
			@Override
			public void process(final IUserMembership userMembership) throws Exception {
				container.access(IGroups.class, new ICallback<IGroups>() {
					@Override
					public void process(IGroups groups) throws Exception {
						userMembershipCallback.process(groups, userMembership);
					}
				});
			}
		}).get();

		container.access(IGroups.class, IUser.class, new ICallback2<IGroups, IUser>() {
			@Override
			public void process(IGroups groups, IUser user) throws Exception {
				groupsCallback.process(groups);
				usersCallback.process(user);
			}
		}).get();

		container.access(IUserMembership.class, IUser.class, IGroups.class, new ICallback3<IUserMembership, IUser, IGroups>() {
			@Override
			public void process(IUserMembership userMembership, IUser user, IGroups groups) throws Exception {
				userMembershipCallback.process(groups, userMembership);
				usersCallback.process(user);
				groupsCallback.process(groups);
			}
		}).get();

		EnsureSameParameter<IComments> commentsCallback = ICallback.Utils.ensureSameParameter();
		container.accessComments(commentsCallback).get();
		container.access(IComments.class, commentsCallback).get();
		container.accessComments(commentsCallback).get();
		assertEquals(3, commentsCallback.count.get());
	}

	@Override
	protected ICrowdSourcedApi getApi() {
		return getServerApi();
	}

}

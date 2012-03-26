package org.softwareFm.crowdsource.user.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.IContainerBuilder;
import org.softwareFm.crowdsource.api.IExtraReaderWriterConfigurator;
import org.softwareFm.crowdsource.api.LocalConfig;
import org.softwareFm.crowdsource.api.MailerMock;
import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.api.user.GroupOperationResult;
import org.softwareFm.crowdsource.api.user.IGroupOperations;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.callbacks.MemoryCallback;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.maps.Maps;

/** This is just checking the wiring: the details of the gui interaction and server side processor are checked elsewhere */
public class ClientGroupOperationsTest extends AbstractProcessorDatabaseIntegrationTests {

	private IContainer localReadWriter;

	@SuppressWarnings("unchecked")
	public void testCreateGroup() {
		localReadWriter.access(IGroupOperations.class, new ICallback<IGroupOperations>() {
			@Override
			public void process(IGroupOperations groupOperations) throws Exception {
				String takeOnEmailList = "a@x.com,b@x.com";
				MemoryCallback<GroupOperationResult> memory = ICallback.Utils.memory();
				groupOperations.createGroup(softwareFmId0, userKey0, "someNewGroup", email0, takeOnEmailList, "subject $email$, $group$", "email $email$, $group$", memory);
				memory.waitUntilCalled(CommonConstants.testTimeOutMs);
				assertEquals(GroupOperationResult.groupId(groupId0), memory.getOnlyResult());
			}
		});

		localReadWriter.access(IGroupsReader.class, IUserReader.class, IUserMembershipReader.class, new IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader groupsReader, IUserReader userReader, IUserMembershipReader userMembershipReader) throws Exception {
				checkUserExistsAndIsMember(userReader, userMembershipReader, softwareFmId0, userKey0, email0, GroupConstants.adminStatus);
				checkUserExistsAndIsMember(userReader, userMembershipReader, softwareFmId1, userKey1, "a@x.com", GroupConstants.invitedStatus);
				checkUserExistsAndIsMember(userReader, userMembershipReader, softwareFmId2, userKey2, "b@x.com", GroupConstants.invitedStatus);

				assertEquals(Arrays.asList(//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.emailKey, email0, "with", "enrich_0"),//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.emailKey, "a@x.com", "with", "enrich_1"),//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId2, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.emailKey, "b@x.com", "with", "enrich_2")), //
						Iterables.list(groupsReader.users(groupId0, groupCryptoKey0)));
				return null;
			}
		});
		MailerMock mailer = getMailer();
		String subject = "subject " + email0 + ", someNewGroup";
		String message = "email " + email0 + ", someNewGroup";

		assertEquals(Arrays.asList(email0, email0), mailer.froms);
		assertEquals(Arrays.asList("a@x.com", "b@x.com"), mailer.tos);
		assertEquals(Lists.times(2, subject), mailer.subjects);
		assertEquals(Lists.times(2, message), mailer.messages);

	}

	public void testInviteGroup() {
		assertEquals(groupId0, createGroup(groupName0, groupCryptoKey0));
		getServerApi().makeUserAndGroupsContainer().modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				groups.addUser(groupId0, groupCryptoKey0, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, LoginConstants.emailKey, email0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus));
				userMembership.addMembership(softwareFmId0, userKey0, groupId0, groupCryptoKey0, GroupConstants.adminStatus);
			}
		});

		localReadWriter.access(IGroupOperations.class, new ICallback<IGroupOperations>() {
			@Override
			public void process(IGroupOperations groupOperations) throws Exception {
				String takeOnEmailList = "a@x.com,b@x.com";
				MemoryCallback<GroupOperationResult> memory = ICallback.Utils.memory();
				groupOperations.inviteToGroup(softwareFmId0, userKey0, groupId0, email0, takeOnEmailList, "subject $email$, $group$", "email $email$, $group$", memory);
				memory.waitUntilCalled(CommonConstants.testTimeOutMs);
				assertEquals(GroupOperationResult.groupId(groupId0), memory.getOnlyResult());

			}
		});

		localReadWriter.access(IGroupsReader.class, IUserReader.class, IUserMembershipReader.class, new IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void>() {
			@SuppressWarnings("unchecked")
			@Override
			public Void apply(IGroupsReader groupsReader, IUserReader userReader, IUserMembershipReader userMembershipReader) throws Exception {
				checkUserExistsAndIsMember(userReader, userMembershipReader, softwareFmId0, userKey0, email0, GroupConstants.adminStatus);
				checkUserExistsAndIsMember(userReader, userMembershipReader, softwareFmId1, userKey1, "a@x.com", GroupConstants.invitedStatus);
				checkUserExistsAndIsMember(userReader, userMembershipReader, softwareFmId2, userKey2, "b@x.com", GroupConstants.invitedStatus);

				List<Map<String, Object>> expected = Arrays.asList(//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.emailKey, email0),//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.emailKey, "a@x.com", "with", "enrich_0"),//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId2, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus, LoginConstants.emailKey, "b@x.com", "with", "enrich_1"));
				List<Map<String, Object>> actual = Iterables.list(groupsReader.users(groupId0, groupCryptoKey0));
				for (int i = 0; i < expected.size(); i++)
					assertEquals(expected.get(i), actual.get(i));
				assertEquals(expected.size(), actual.size());
				return null;
			}

		});
		MailerMock mailer = getMailer();
		String subject = "subject " + email0 + ", groupId0Name";
		String message = "email " + email0 + ", groupId0Name";

		assertEquals(Arrays.asList(email0, email0), mailer.froms);
		assertEquals(Arrays.asList("a@x.com", "b@x.com"), mailer.tos);
		assertEquals(Lists.times(2, subject), mailer.subjects);
		assertEquals(Lists.times(2, message), mailer.messages);

	}

	public void testAcceptInvite() {
		assertEquals(softwareFmId1, createUser());
		assertEquals(groupId0, createGroup(groupName0, groupCryptoKey0));
		getServerApi().makeUserAndGroupsContainer().modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				groups.addUser(groupId0, groupCryptoKey0, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, LoginConstants.emailKey, email0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus));
				userMembership.addMembership(softwareFmId0, userKey0, groupId0, groupCryptoKey0, GroupConstants.adminStatus);

				groups.addUser(groupId0, groupCryptoKey0, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId1, LoginConstants.emailKey, email1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus));
				userMembership.addMembership(softwareFmId1, userKey1, groupId0, groupCryptoKey0, GroupConstants.invitedStatus);
			}
		});

		localReadWriter.access(IGroupOperations.class, new ICallback<IGroupOperations>() {
			@Override
			public void process(IGroupOperations groupOperations) throws Exception {
				MemoryCallback<GroupOperationResult> memory = ICallback.Utils.memory();
				groupOperations.acceptInvite(softwareFmId1, userKey1, groupId0, memory);
				memory.waitUntilCalled(CommonConstants.testTimeOutMs);
				assertEquals(GroupOperationResult.groupId(groupId0), memory.getOnlyResult());
			}
		});

		localReadWriter.access(IGroupsReader.class, IUserReader.class, IUserMembershipReader.class, new IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void>() {
			@SuppressWarnings("unchecked")
			@Override
			public Void apply(IGroupsReader groupsReader, IUserReader userReader, IUserMembershipReader userMembershipReader) throws Exception {
				checkUserExistsAndIsMember(userReader, userMembershipReader, softwareFmId0, userKey0, email0, GroupConstants.adminStatus);
				checkUserExistsAndIsMember(userReader, userMembershipReader, softwareFmId1, userKey1, email1, GroupConstants.memberStatus);

				List<Map<String, Object>> expected = Arrays.asList(//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.emailKey, email0),//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId1, GroupConstants.membershipStatusKey, GroupConstants.memberStatus, LoginConstants.emailKey, email1));
				List<Map<String, Object>> actual = Iterables.list(groupsReader.users(groupId0, groupCryptoKey0));
				for (int i = 0; i < expected.size(); i++)
					assertEquals(expected.get(i), actual.get(i));
				assertEquals(expected.size(), actual.size());
				return null;
			}

		});
	}

	public void testLEaveGroup() {
		assertEquals(softwareFmId1, createUser());
		assertEquals(groupId0, createGroup(groupName0, groupCryptoKey0));
		getServerApi().makeUserAndGroupsContainer().modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				groups.addUser(groupId0, groupCryptoKey0, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, LoginConstants.emailKey, email0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus));
				userMembership.addMembership(softwareFmId0, userKey0, groupId0, groupCryptoKey0, GroupConstants.adminStatus);

				groups.addUser(groupId0, groupCryptoKey0, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId1, LoginConstants.emailKey, email1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus));
				userMembership.addMembership(softwareFmId1, userKey1, groupId0, groupCryptoKey0, GroupConstants.memberStatus);
			}
		});

		localReadWriter.access(IGroupOperations.class, new ICallback<IGroupOperations>() {
			@Override
			public void process(IGroupOperations groupOperations) throws Exception {
				MemoryCallback<GroupOperationResult> memory = ICallback.Utils.memory();
				groupOperations.leaveGroup(softwareFmId1, userKey1, groupId0, memory);
				memory.waitUntilCalled(CommonConstants.testTimeOutMs);
				assertEquals(GroupOperationResult.groupId(groupId0), memory.getOnlyResult());
			}
		});

		localReadWriter.access(IGroupsReader.class, IUserReader.class, IUserMembershipReader.class, new IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void>() {
			@SuppressWarnings("unchecked")
			@Override
			public Void apply(IGroupsReader groupsReader, IUserReader userReader, IUserMembershipReader userMembershipReader) throws Exception {
				checkUserExistsAndIsMember(userReader, userMembershipReader, softwareFmId0, userKey0, email0, GroupConstants.adminStatus);
				assertEquals(Collections.emptyList(), userMembershipReader.walkGroupsFor(softwareFmId1, userKey1));

				List<Map<String, Object>> expected = Arrays.asList(//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.emailKey, email0));//
				assertEquals(expected, Iterables.list(groupsReader.users(groupId0, groupCryptoKey0)));
				return null;
			}

		});

	}

	public void testKickFromGroup() {
		assertEquals(softwareFmId1, createUser());
		assertEquals(groupId0, createGroup(groupName0, groupCryptoKey0));
		getServerApi().makeUserAndGroupsContainer().modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				groups.addUser(groupId0, groupCryptoKey0, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, LoginConstants.emailKey, email0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus));
				userMembership.addMembership(softwareFmId0, userKey0, groupId0, groupCryptoKey0, GroupConstants.adminStatus);

				groups.addUser(groupId0, groupCryptoKey0, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId1, LoginConstants.emailKey, email1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus));
				userMembership.addMembership(softwareFmId1, userKey1, groupId0, groupCryptoKey0, GroupConstants.memberStatus);
			}
		});

		localReadWriter.access(IGroupOperations.class, new ICallback<IGroupOperations>() {
			@Override
			public void process(IGroupOperations groupOperations) throws Exception {
				MemoryCallback<GroupOperationResult> memory = ICallback.Utils.memory();
				groupOperations.kickFromGroup(softwareFmId0, userKey0, groupId0, softwareFmId1, memory);
				memory.waitUntilCalled(CommonConstants.testTimeOutMs);
				assertEquals(GroupOperationResult.groupId(groupId0), memory.getOnlyResult());
			}
		});

		localReadWriter.access(IGroupsReader.class, IUserReader.class, IUserMembershipReader.class, new IFunction3<IGroupsReader, IUserReader, IUserMembershipReader, Void>() {
			@SuppressWarnings("unchecked")
			@Override
			public Void apply(IGroupsReader groupsReader, IUserReader userReader, IUserMembershipReader userMembershipReader) throws Exception {
				checkUserExistsAndIsMember(userReader, userMembershipReader, softwareFmId0, userKey0, email0, GroupConstants.adminStatus);
				assertEquals(Collections.emptyList(), userMembershipReader.walkGroupsFor(softwareFmId1, userKey1));

				List<Map<String, Object>> expected = Arrays.asList(//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.emailKey, email0));//
				assertEquals(expected, Iterables.list(groupsReader.users(groupId0, groupCryptoKey0)));
				return null;
			}

		});
	}

	@SuppressWarnings("unchecked")
	private void checkUserExistsAndIsMember(IUserReader userReader, IUserMembershipReader userMembershipReader, String id, String key, String email, String status) {
		assertEquals(email, userReader.getUserProperty(id, key, LoginConstants.emailKey));
		assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId0, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, status)), userMembershipReader.walkGroupsFor(id, key));
	}

	@Override
	protected IExtraReaderWriterConfigurator<LocalConfig> getLocalExtraReaderWriterConfigurator() {
		return new IExtraReaderWriterConfigurator<LocalConfig>() {
			@Override
			public void builder(IContainerBuilder builder,LocalConfig apiConfig) {
				builder.register(IGroupOperations.class, IGroupOperations.Utils.clientGroupOperations(builder, CommonConstants.testTimeOutMs));
			}
		};
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		localReadWriter = getLocalApi().makeContainer();
		assertEquals(softwareFmId0, createUser());
	}

}

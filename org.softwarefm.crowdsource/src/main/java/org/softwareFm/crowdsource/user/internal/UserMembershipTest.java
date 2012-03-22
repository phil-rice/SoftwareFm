/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.user.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.ICrowdSourcedReadWriteApi;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class UserMembershipTest extends ApiTest {

	private final String groupId1 = "groupId1";
	private final String groupId2 = "groupId2";
	private final String groupId3 = "groupId3";

	private String groupCrypto1;
	private String groupCrypto2;
	private String groupCrypto3;
	private String user1Id;
	private ICrowdSourcedReadWriteApi serverReadWriter;
	private ICrowdSourcedReadWriteApi localReadWriter;

	@SuppressWarnings("unchecked")
	public void testAddMembership() {
		final List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCrypto1, GroupConstants.membershipStatusKey, "someStatus1"),//
				Maps.stringObjectMap(GroupConstants.groupIdKey, groupId2, GroupConstants.groupCryptoKey, groupCrypto2, GroupConstants.membershipStatusKey, "someStatus2"));
		serverReadWriter.modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups first, IUserMembership membershipForServer) throws Exception {
				membershipForServer.addMembership(user1Id, userKey0, groupId1, groupCrypto1, "someStatus1");
				membershipForServer.addMembership(user1Id, userKey0, groupId2, groupCrypto2, "someStatus2");

				assertEquals(expected, membershipForServer.walkGroupsFor(user1Id, userKey0));
			}
		});

		checkLocalMembership(expected);
	}

	private void checkLocalMembership(final List<Map<String, Object>> expected) {
		localReadWriter.accessUserMembershipReader(new IFunction2<IGroupsReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader from1, IUserMembershipReader membershipForLocal) throws Exception {
				assertEquals(expected, membershipForLocal.walkGroupsFor(user1Id, userKey0));
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void testRemoveUser() {
		final List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCrypto1, GroupConstants.membershipStatusKey, "someStatus1"),//
				Maps.stringObjectMap(GroupConstants.groupIdKey, groupId3, GroupConstants.groupCryptoKey, groupCrypto3, GroupConstants.membershipStatusKey, "someStatus3"));
		serverReadWriter.modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups first, IUserMembership membershipForServer) throws Exception {
				membershipForServer.addMembership(user1Id, userKey0, groupId1, groupCrypto1, "someStatus1");
				membershipForServer.addMembership(user1Id, userKey0, groupId2, groupCrypto2, "someStatus2");
				membershipForServer.addMembership(user1Id, userKey0, groupId3, groupCrypto3, "someStatus3");
				membershipForServer.remove(user1Id, userKey0, groupId2, groupCrypto2);

				assertEquals(expected, membershipForServer.walkGroupsFor(user1Id, userKey0));
			}
		});

		checkLocalMembership(expected);

	}

	@SuppressWarnings("unchecked")
	public void testRemoveUserWhenGroupIsntPresent() {
		final List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCrypto1, GroupConstants.membershipStatusKey, "someStatus1"),//
				Maps.stringObjectMap(GroupConstants.groupIdKey, groupId3, GroupConstants.groupCryptoKey, groupCrypto3, GroupConstants.membershipStatusKey, "someStatus3"));
		serverReadWriter.modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups first, IUserMembership membershipForServer) throws Exception {
				membershipForServer.addMembership(user1Id, userKey0, groupId1, groupCrypto1, "someStatus1");
				membershipForServer.addMembership(user1Id, userKey0, groupId3, groupCrypto3, "someStatus3");

				membershipForServer.remove(user1Id, userKey0, groupId2, groupCrypto2);

				assertEquals(expected, membershipForServer.walkGroupsFor(user1Id, userKey0));
				assertEquals(expected, membershipForServer.walkGroupsFor(user1Id, userKey0));
			}
		});

		checkLocalMembership(expected);
	}

	public void testCannotAddSameGroupTwice() {
		serverReadWriter.modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups first, final IUserMembership membershipForServer) throws Exception {
				membershipForServer.addMembership(user1Id, userKey0, groupId1, groupCrypto1, "someStatus1");
				Tests.assertThrowsWithMessage("Already a member of group. Sfm Id " + user1Id + ". Group Id groupId1", RuntimeException.class, new Runnable() {
					@Override
					public void run() {
						membershipForServer.addMembership(user1Id, userKey0, groupId1, groupCrypto1, "someStatus1");
					}
				});
			}
		});
	}

	public void testMembershipCryptoIsDefaultUserValue() {
		serverReadWriter.accessUserReader(new IFunction1<IUserReader, Void>() {
			@Override
			public Void apply(IUserReader user) throws Exception {
				assertNotNull(user.getUserProperty(user1Id, userKey0, GroupConstants.membershipCryptoKey));
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void testSetGetMembershipProperty() {
		serverReadWriter.modify(IUserMembership.class, new ICallback<IUserMembership>() {
			@Override
			public void process(final IUserMembership membershipForServer) throws Exception {
				localReadWriter.access(IUserMembershipReader.class, IGitReader.class, new IFunction2<IUserMembershipReader, IGitReader, Void>() {
					@Override
					public Void apply(IUserMembershipReader membershipForLocal, IGitReader gitReader) throws Exception {
						membershipForServer.addMembership(user1Id, userKey0, groupId1, groupCrypto1, "someStatus1");
						membershipForServer.addMembership(user1Id, userKey0, groupId2, groupCrypto2, "someStatus2");

						membershipForServer.setMembershipProperty(user1Id, userKey0, groupId1, "someProperty", "value1");
						assertEquals("value1", membershipForServer.getMembershipProperty(user1Id, userKey0, groupId1, "someProperty"));
						assertEquals("value1", membershipForLocal.getMembershipProperty(user1Id, userKey0, groupId1, "someProperty"));

						membershipForServer.setMembershipProperty(user1Id, userKey0, groupId1, "someProperty", "value2");
						assertEquals("value2", membershipForServer.getMembershipProperty(user1Id, userKey0, groupId1, "someProperty"));
						assertEquals("value1", membershipForLocal.getMembershipProperty(user1Id, userKey0, groupId1, "someProperty"));// oftens intermittantly fails

						gitReader.clearCaches();
						assertEquals("value2", membershipForServer.getMembershipProperty(user1Id, userKey0, groupId1, "someProperty"));
						assertEquals("value2", membershipForLocal.getMembershipProperty(user1Id, userKey0, groupId1, "someProperty"));

						List<Map<String, Object>> expected = Arrays.asList(//
								Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCrypto1, GroupConstants.membershipStatusKey, "someStatus1", "someProperty", "value2"),//
								Maps.stringObjectMap(GroupConstants.groupIdKey, groupId2, GroupConstants.groupCryptoKey, groupCrypto2, GroupConstants.membershipStatusKey, "someStatus2"));

						assertEquals(expected, membershipForServer.walkGroupsFor(user1Id, userKey0));
						assertEquals(expected, membershipForLocal.walkGroupsFor(user1Id, userKey0));
						return null;
					}
				});
			}
		});

	}

	public void testSetMembershipPropertyThrowsExceptionIfGroupNotFound() {
		serverReadWriter.modify(IUserMembership.class, new ICallback<IUserMembership>() {
			@Override
			public void process(final IUserMembership membershipForServer) throws Exception {
				membershipForServer.addMembership(user1Id, userKey0, groupId1, groupCrypto1, "someStatus1");
				Tests.assertThrowsWithMessage("Cannot find group id groupId2 for user someNewSoftwareFmId0", IllegalArgumentException.class, new Runnable() {
					@Override
					public void run() {
						membershipForServer.setMembershipProperty(user1Id, userKey0, groupId2, "someProperty", "value2");
					}
				});
			}
		});

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		truncateUsersTable();
		getServerApi().getServer();
		user1Id = createUser();
		serverReadWriter = getServerApi().makeReadWriter();
		localReadWriter = getLocalApi().makeReadWriter();
	}
}
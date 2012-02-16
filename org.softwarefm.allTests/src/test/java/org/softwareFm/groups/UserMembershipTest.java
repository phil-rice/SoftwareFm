/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.groups;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.tests.Tests;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.UserMembershipReaderForLocal;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.softwareFmServer.UserMembershipForServer;

public class UserMembershipTest extends GitTest {

	private UserMembershipReaderForLocal membershipForLocal;
	private UserMembershipForServer membershipForServer;

	private String groupId1;
	private String groupId2;

	private String groupCrypto1;
	private String groupCrypto2;
	private String user1Id;
	private IUser user;
	private String userCrypto;

	@SuppressWarnings("unchecked")
	public void testAddMembership() {
		membershipForServer.addMembership(user1Id, groupId1, groupCrypto1, "someStatus1");
		membershipForServer.addMembership(user1Id, groupId2, groupCrypto2, "someStatus2");

		List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCrypto1, GroupConstants.membershipStatusKey, "someStatus1"),//
				Maps.stringObjectMap(GroupConstants.groupIdKey, groupId2, GroupConstants.groupCryptoKey, groupCrypto2, GroupConstants.membershipStatusKey, "someStatus2"));

		assertEquals(expected, membershipForServer.walkGroupsFor(user1Id));
		assertEquals(expected, membershipForLocal.walkGroupsFor(user1Id));
	}

	public void testCannotAddSameGroupTwice() {
		membershipForServer.addMembership(user1Id, groupId1, groupCrypto1, "someStatus1");
		Tests.assertThrowsWithMessage(groupId1, RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				membershipForServer.addMembership(user1Id, groupId1, groupCrypto1, "someStatus1");
			}
		});
	}

	public void testAddingMembershipAddsPropertyToUser() {
		membershipForServer.addMembership(user1Id, groupId1, groupCrypto1, "someStatus1");
		assertNotNull(user.getUserProperty(user1Id, userCrypto, GroupConstants.membershipCryptoKey));
	}

	@SuppressWarnings("unchecked")
	public void testSetGetMembershipProperty() {
		membershipForServer.addMembership(user1Id, groupId1, groupCrypto1, "someStatus1");
		membershipForServer.addMembership(user1Id, groupId2, groupCrypto2, "someStatus2");

		membershipForServer.setMembershipProperty(user1Id, groupId1, "someProperty", "value1");
		assertEquals("value1", membershipForServer.getMembershipProperty(user1Id, groupId1, "someProperty"));
		assertEquals("value1", membershipForLocal.getMembershipProperty(user1Id, groupId1, "someProperty"));

		membershipForServer.setMembershipProperty(user1Id, groupId1, "someProperty", "value2");
		assertEquals("value2", membershipForServer.getMembershipProperty(user1Id, groupId1, "someProperty"));
		assertEquals("value1", membershipForLocal.getMembershipProperty(user1Id, groupId1, "someProperty"));

		gitLocal.clearCaches();
		assertEquals("value2", membershipForServer.getMembershipProperty(user1Id, groupId1, "someProperty"));
		assertEquals("value2", membershipForLocal.getMembershipProperty(user1Id, groupId1, "someProperty"));

		List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCrypto1, GroupConstants.membershipStatusKey, "someStatus1", "someProperty", "value2"),//
				Maps.stringObjectMap(GroupConstants.groupIdKey, groupId2, GroupConstants.groupCryptoKey, groupCrypto2, GroupConstants.membershipStatusKey, "someStatus2"));

		assertEquals(expected, membershipForServer.walkGroupsFor(user1Id));
		assertEquals(expected, membershipForLocal.walkGroupsFor(user1Id));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userCrypto = Crypto.makeKey();
		groupId1 = "groupId1";
		groupId2 = "groupId2";
		groupCrypto1 = Crypto.makeKey();
		groupCrypto2 = Crypto.makeKey();
		user1Id = "sfmId1";

		IUrlGenerator userUrlGenerator = LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix);
		IFunction1<String, String> repoDefn = Strings.firstNSegments(3);
		user = ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, repoDefn,Maps.<String,Callable<Object>>makeMap( GroupConstants.membershipCryptoKey, Callables.makeCryptoKey()), SoftwareFmConstants.urlPrefix);
		membershipForLocal = new UserMembershipReaderForLocal(userUrlGenerator, gitLocal, user, userCrypto);

		IFunction1<Map<String, Object>, String> userCryptoFn = Functions.constant(userCrypto);
		membershipForServer = new UserMembershipForServer(userUrlGenerator, remoteOperations, user, userCryptoFn, repoDefn);
	}
}
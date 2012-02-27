/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.IUser;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.tests.Tests;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IUserMembership;

public class TakeOnProcessorTest extends GitTest {

	private final String groupName = "someNewGroupName";

	private IGroups groups;
	private IUser user;
	private TakeOnProcessor takeOnProcessor;
	private IUrlGenerator groupUrlGenerator;

	private String crypto1;
	private String crypto2;

	private IUserMembership membership;

	public void testCreateGroup() {
		String groupCrypto = Crypto.makeKey();
		String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
		String groupUrl = groupUrlGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(groupUrl, CommonConstants.dataFileName, groupCrypto);
		Map<String, Object> expected = Maps.stringObjectMap(GroupConstants.groupNameKey, groupName);
		assertEquals(expected, remoteOperations.getFile(fileDescription));
		assertEquals(expected, gitLocal.getFile(fileDescription));
	}

	@SuppressWarnings("unchecked")
	public void testAddUserToGroup() {
		String projectCrypto1 = Crypto.makeKey();
		String projectCrypto2 = Crypto.makeKey();

		user.setUserProperty("sfm1", crypto1, SoftwareFmConstants.projectCryptoKey, projectCrypto1);
		user.setUserProperty("sfm2", crypto2, SoftwareFmConstants.projectCryptoKey, projectCrypto2);

		String groupCrypto = Crypto.makeKey();
		String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
		takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "sfm1","email1", "someStatus");
		takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "sfm2","email2", "someStatus");

		List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(LoginConstants.emailKey, "email1", LoginConstants.softwareFmIdKey, "sfm1", SoftwareFmConstants.projectCryptoKey, projectCrypto1, GroupConstants.membershipStatusKey,  "someStatus"), //
				Maps.stringObjectMap(LoginConstants.emailKey, "email2", LoginConstants.softwareFmIdKey, "sfm2", SoftwareFmConstants.projectCryptoKey, projectCrypto2, GroupConstants.membershipStatusKey,  "someStatus"));
		List<Map<String, Object>> actualUsers = Iterables.list(groups.users(groupId, groupCrypto));
		assertEquals(expected, actualUsers);

		assertEquals(projectCrypto1, user.getUserProperty("sfm1", crypto1, SoftwareFmConstants.projectCryptoKey));
		assertEquals(projectCrypto2, user.getUserProperty("sfm2", crypto2, SoftwareFmConstants.projectCryptoKey));
	}

	@SuppressWarnings("unchecked")
	public void testAddingUserToGroupUpdatesMembership() {
		String groupCrypto = Crypto.makeKey();
		String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
		takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto,"sfm1", "email1", "someStatus");
		takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "sfm2","email2", "someStatus");
		List<Map<String, Object>> expected = Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCrypto, GroupConstants.membershipStatusKey,  "someStatus"));
		assertEquals(expected, membership.walkGroupsFor("sfm1", crypto1));
		assertEquals(expected, membership.walkGroupsFor("sfm2", crypto2));
	}

	@SuppressWarnings("unchecked")
	public void testAddUserToGroupAddsProjectCryptoIfItDoesntExist() {
		String groupCrypto = Crypto.makeKey();
		String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
		takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "sfm1","email1", "someStatus");
		takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "sfm2","email2", "someStatus");

		String projectCrypto1 = user.getUserProperty("sfm1", crypto1, SoftwareFmConstants.projectCryptoKey);
		String projectCrypto2 = user.getUserProperty("sfm2", crypto2, SoftwareFmConstants.projectCryptoKey);

		List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(LoginConstants.emailKey, "email1", LoginConstants.softwareFmIdKey, "sfm1", SoftwareFmConstants.projectCryptoKey, projectCrypto1, GroupConstants.membershipStatusKey, "someStatus"), //
				Maps.stringObjectMap(LoginConstants.emailKey, "email2", LoginConstants.softwareFmIdKey, "sfm2", SoftwareFmConstants.projectCryptoKey, projectCrypto2, GroupConstants.membershipStatusKey,  "someStatus"));
		List<Map<String, Object>> actualUsers = Iterables.list(groups.users(groupId, groupCrypto));
		assertEquals(expected, actualUsers);
	}

	public void testWhenUserDoesntExist() {
		final String groupCrypto = Crypto.makeKey();
		final String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
		Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "userDoesntExist","emailDoesntExist", "someStatus");
			}
		});
	}

	public void testAddUserToGroupWhenGroupDoesntExist() {
		final String groupCrypto = Crypto.makeKey();
		final String groupId = "groupDoesntExist";
		Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "sfm1","email1", "someStatus");
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IUrlGenerator groupsUrlGenerator = GroupConstants.groupsGenerator(SoftwareFmConstants.urlPrefix);
		IUrlGenerator userUrlGenerator = LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix);
		groups = new GroupsForServer(groupsUrlGenerator, remoteOperations, Strings.firstNSegments(3));
		user = SoftwareFmServer.makeUser(remoteOperations, userUrlGenerator, SoftwareFmServer.makeDefaultProperties());
		crypto1 = Crypto.makeKey();
		crypto2 = Crypto.makeKey();
		IFunction1<Map<String, Object>, String> cryptoFn = new IFunction1<Map<String, Object>, String>() {
			private final Map<String, String> map = Maps.makeImmutableMap("sfm1", crypto1, "sfm2", crypto2);

			@Override
			public String apply(Map<String, Object> from) throws Exception {
				return map.get(from.get(LoginConstants.softwareFmIdKey));
			}
		};
		Callable<String> groupIdGenerator = Callables.uuidGenerator();
		IFunction1<String, String> repoDefnFn = Strings.firstNSegments(3);
		groupUrlGenerator = groupsUrlGenerator;
		membership = new UserMembershipForServer(userUrlGenerator, user, remoteOperations, repoDefnFn);
		takeOnProcessor = new TakeOnProcessor(remoteOperations, user, membership, groups, cryptoFn, groupUrlGenerator, groupIdGenerator, repoDefnFn);
	}
}
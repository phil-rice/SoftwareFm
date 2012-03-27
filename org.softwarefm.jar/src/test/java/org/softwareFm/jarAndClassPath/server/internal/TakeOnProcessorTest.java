/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.server.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.ITakeOnEnrichmentProvider;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.server.ITakeOnProcessor;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.softwareFm.jarAndClassPath.api.ISoftwareFmApiFactory.Utils;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;

public class TakeOnProcessorTest extends ApiTest {

	String groupName = "someGroupName";

	private ITakeOnProcessor takeOnProcessor;

	public void testCreateGroup() {
		String groupCrypto = getCryptoGenerators().groupCrypto();
		String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
		String groupUrl = getServerConfig().groupUrlGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(groupUrl, CommonConstants.dataFileName, groupCrypto);
		Map<String, Object> expected = Maps.stringObjectMap(GroupConstants.groupNameKey, groupName);
		assertEquals(expected, remoteOperations.getFile(fileDescription));
		assertEquals(expected, IGitReader.Utils.getFileAsMap(getLocalApi().makeContainer(), fileDescription));
	}

	@SuppressWarnings("unchecked")
	public void testAddUserToGroup() {
		final String softwareFmId1 = createUser();
		final String projectCrypto1 = Tests.assertNotNull(getUserProperty(softwareFmId1, userKey0, JarAndPathConstants.projectCryptoKey));
		final String email1 = Tests.assertNotNull(getUserProperty(softwareFmId1, userKey0, LoginConstants.emailKey));

		final String softwareFmId2 = createUser();
		final String projectCrypto2 = Tests.assertNotNull(getUserProperty(softwareFmId2, userKey1, JarAndPathConstants.projectCryptoKey));
		final String email2 = Tests.assertNotNull(getUserProperty(softwareFmId2, userKey1, LoginConstants.emailKey));

		final String groupCrypto = getCryptoGenerators().groupCrypto();
		final String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);

		takeOnProcessor.addExistingUserToGroup(groupId, groupCrypto, softwareFmId1, email1, "someStatus");
		takeOnProcessor.addExistingUserToGroup(groupId, groupCrypto, softwareFmId2, email2, "someStatus");

		getServerUserAndGroupsContainer().accessGroupReader(new IFunction1<IGroupsReader, Void>() {
			@Override
			public Void apply(IGroupsReader groups) throws Exception {
				List<Map<String, Object>> expected = Arrays.asList(//
						Maps.stringObjectMap(LoginConstants.emailKey, email1, LoginConstants.softwareFmIdKey, softwareFmId1, JarAndPathConstants.projectCryptoKey, projectCrypto1, GroupConstants.membershipStatusKey, "someStatus"), //
						Maps.stringObjectMap(LoginConstants.emailKey, email2, LoginConstants.softwareFmIdKey, softwareFmId2, JarAndPathConstants.projectCryptoKey, projectCrypto2, GroupConstants.membershipStatusKey, "someStatus"));
				List<Map<String, Object>> actualUsers = Iterables.list(groups.users(groupId, groupCrypto));
				assertEquals(expected, actualUsers);
				return null;
			}
		}, ICallback.Utils.<Void>noCallback()).get();
	}

	@SuppressWarnings("unchecked")
	public void testAddingUserToGroupUpdatesMembership() {
		final String softwareFmId1 = createUser();
		final String email1 = Tests.assertNotNull(getUserProperty(softwareFmId1, userKey0, LoginConstants.emailKey));

		final String softwareFmId2 = createUser();
		final String email2 = Tests.assertNotNull(getUserProperty(softwareFmId2, userKey1, LoginConstants.emailKey));

		final String groupCrypto = getCryptoGenerators().groupCrypto();
		final String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);

		takeOnProcessor.addExistingUserToGroup(groupId, groupCrypto, softwareFmId1, email1, "someStatus");
		takeOnProcessor.addExistingUserToGroup(groupId, groupCrypto, softwareFmId2, email2, "someStatus");
		final List<Map<String, Object>> expected = Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCrypto, GroupConstants.membershipStatusKey, "someStatus"));
		getServerUserAndGroupsContainer().accessUserMembershipReader(new IFunction2<IGroupsReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader from1, IUserMembershipReader membership) throws Exception {
				assertEquals(expected, membership.walkGroupsFor(softwareFmId1, userKey0));
				assertEquals(expected, membership.walkGroupsFor(softwareFmId2, userKey1));
				return null;
			}
		}, ICallback.Utils.<Void>noCallback()).get();
	}

	public void testWhenUserDoesntExist() {
		final String groupCrypto = Crypto.makeKey();
		final String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
		Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				takeOnProcessor.addExistingUserToGroup(groupId, groupCrypto, "userDoesntExist", "emailDoesntExist", "someStatus");
			}
		});
	}

	public void testAddUserToGroupWhenGroupDoesntExist() {
		final String groupCrypto = Crypto.makeKey();
		final String groupId = "groupDoesntExist";
		Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				takeOnProcessor.addExistingUserToGroup(groupId, groupCrypto, "sfm1", "email1", "someStatus");
			}
		});
	}

	@Override
	protected Map<String, Callable<Object>> getDefaultUserValues() {
		return Maps.with(super.getDefaultUserValues(), JarAndPathConstants.projectCryptoKey, Callables.makeCryptoKey());
	}

	@Override
	protected ITakeOnEnrichmentProvider getTakeOnEnrichment() {
		return Utils.getTakeOnEnrichment();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getServerApi().getServer();
		truncateUsersTable();
		takeOnProcessor = getServerDoers().getTakeOnProcessor();
	}
}
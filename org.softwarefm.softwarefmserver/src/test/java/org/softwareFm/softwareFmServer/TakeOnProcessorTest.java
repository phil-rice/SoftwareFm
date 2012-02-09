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
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.tests.Tests;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.server.ICrowdSourcedServer;

public class TakeOnProcessorTest extends GitTest {

	private final String groupName = "someNewGroupName";

	private IGroups groups;
	private IUser user;
	private TakeOnProcessor takeOnProcessor;
	private IUrlGenerator groupUrlGenerator;

	private String crypto1;

	private String crypto2;

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
		takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "email1");
		takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "email2");
		
		
		List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(LoginConstants.emailKey, "email1", LoginConstants.softwareFmIdKey, "sfm1", SoftwareFmConstants.projectCryptoKey, projectCrypto1, GroupConstants.userStatusInGroup, GroupConstants.invitedStatus), //
				Maps.stringObjectMap(LoginConstants.emailKey, "email2", LoginConstants.softwareFmIdKey, "sfm2", SoftwareFmConstants.projectCryptoKey, projectCrypto2, GroupConstants.userStatusInGroup, GroupConstants.invitedStatus));
		List<Map<String, Object>> actualUsers = Iterables.list(groups.users(groupId, groupCrypto));
		assertEquals(expected, actualUsers);

		assertEquals(projectCrypto1, user.getUserProperty("sfm1", crypto1, SoftwareFmConstants.projectCryptoKey));
		assertEquals(projectCrypto2, user.getUserProperty("sfm2", crypto2, SoftwareFmConstants.projectCryptoKey));
	}

	@SuppressWarnings("unchecked")
	public void testAddUserToGroupAddsProjectCryptoIfItDoesntExist() {
		String groupCrypto = Crypto.makeKey();
		String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
		takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "email1");
		takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "email2");
		
		String projectCrypto1 = user.getUserProperty("sfm1", crypto1, SoftwareFmConstants.projectCryptoKey);
		String projectCrypto2 = user.getUserProperty("sfm2", crypto2, SoftwareFmConstants.projectCryptoKey);
		
		List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(LoginConstants.emailKey, "email1", LoginConstants.softwareFmIdKey, "sfm1", SoftwareFmConstants.projectCryptoKey, projectCrypto1, GroupConstants.userStatusInGroup, GroupConstants.invitedStatus), //
				Maps.stringObjectMap(LoginConstants.emailKey, "email2", LoginConstants.softwareFmIdKey, "sfm2", SoftwareFmConstants.projectCryptoKey, projectCrypto2, GroupConstants.userStatusInGroup, GroupConstants.invitedStatus));
		List<Map<String, Object>> actualUsers = Iterables.list(groups.users(groupId, groupCrypto));
		assertEquals(expected, actualUsers);
	}

	public void testWhenUserDoesntExist() {
		final String groupCrypto = Crypto.makeKey();
		final String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
		Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "emailDoesntExist");
			}
		});
	}

	public void testAddUserToGroupWhenGroupDoesntExist() {
		final String groupCrypto = Crypto.makeKey();
		final String groupId = "groupDoesntExist";
		Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, "email1");
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		groups = new GroupForServer(GroupConstants.groupsGenerator(), remoteOperations, Strings.firstNSegments(3));
		user = ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, Strings.firstNSegments(3));
		crypto1 = Crypto.makeKey();
		crypto2 = Crypto.makeKey();
		IFunction1<Map<String, Object>, String> cryptoFn = new IFunction1<Map<String, Object>, String>() {
			private final Map<String, String> map = Maps.makeImmutableMap("email1", crypto1, "email2", crypto2);

			@Override
			public String apply(Map<String, Object> from) throws Exception {
				return map.get(from.get(LoginConstants.emailKey));
			}
		};
		IFunction1<String, String> emailToSoftware = Functions.map("email1", "sfm1", "email2", "sfm2");
		Callable<String> groupIdGenerator = Callables.uuidGenerator();
		IFunction1<String, String> repoDefnFn = Strings.firstNSegments(3);
		groupUrlGenerator = GroupConstants.groupsGenerator();
		takeOnProcessor = new TakeOnProcessor(remoteOperations, user, groups, cryptoFn, emailToSoftware, Callables.makeCryptoKey(), groupUrlGenerator, groupIdGenerator, repoDefnFn);
	}
}

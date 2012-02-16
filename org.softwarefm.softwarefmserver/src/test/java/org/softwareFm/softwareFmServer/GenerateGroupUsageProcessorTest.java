/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.util.Arrays;
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
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IUsageReader;
import org.softwareFm.eclipse.user.IUserMembership;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.processors.IGenerateUsageReportGenerator;

public class GenerateGroupUsageProcessorTest extends GitTest {

	private GroupsForServer remoteGroups;
	private IGenerateUsageReportGenerator generateUsageReportGenerator;
	private IUser user;
	private String groupId;
	private String groupCryptoKey;
	private String sfmId1;
	private String sfmId2;
	private String user1projectCrypto;
	private String user2projectCrypto;
	private String userCrypto1;
	private String userCrypto2;
	private ProjectForServer project;
	private TakeOnProcessor takeOnProcessor;
	private String email1;
	private String email2;

	public void test() {
		String groupId = takeOnProcessor.createGroup("someGroupName", groupCryptoKey);
		assertEquals(this.groupId, groupId);// testing setup
		takeOnProcessor.addExistingUserToGroup(groupId, "someGroupName", groupCryptoKey, "email1");
		takeOnProcessor.addExistingUserToGroup(groupId, "someGroupName", groupCryptoKey, "email2");

		project.addProjectDetails(sfmId1, "gid1", "aid1", "month1", 1);
		project.addProjectDetails(sfmId1, "gid1", "aid1", "month1", 2);
		project.addProjectDetails(sfmId1, "gid1", "aid2", "month2", 1);
		project.addProjectDetails(sfmId1, "gid1", "aid3", "month2", 1);

		project.addProjectDetails(sfmId2, "gid1", "aid1", "month1", 1);
		project.addProjectDetails(sfmId2, "gid1", "aid2", "month1", 2);
		project.addProjectDetails(sfmId2, "gid1", "aid4", "month2", 1);
		project.addProjectDetails(sfmId2, "gid1", "aid5", "month2", 1);

		GenerateGroupUsageProcessor processor = new GenerateGroupUsageProcessor(remoteOperations, generateUsageReportGenerator, remoteGroups);
		processor.execute(GroupConstants.generateGroupReportPrefix, Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.monthKey, "month1", GroupConstants.groupCryptoKey, groupCryptoKey));
		Map<String, Object> month1Report = remoteGroups.getUsageReport(groupId, groupCryptoKey, "month1");
		Map<String, Object> month2Report = remoteGroups.getUsageReport(groupId, groupCryptoKey, "month2");
		assertEquals(Maps.stringObjectMap("gid1", Maps.stringObjectMap(//
				"aid1", Maps.stringObjectMap(sfmId1, Arrays.asList(1l, 2l), sfmId2, Arrays.asList(1l)),//
				"aid2", Maps.stringObjectMap(sfmId2, Arrays.asList(2l)))), month1Report);
		assertEquals(null, month2Report);

		processor.execute(GroupConstants.generateGroupReportPrefix, Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.monthKey, "month2", GroupConstants.groupCryptoKey, groupCryptoKey));
		Map<String, Object> month2SecondReport = remoteGroups.getUsageReport(groupId, groupCryptoKey, "month2");
		assertEquals(Maps.stringObjectMap("gid1", Maps.stringObjectMap(//
				"aid2", Maps.stringObjectMap(sfmId1, Arrays.asList(1l)), //
				"aid3", Maps.stringObjectMap(sfmId1, Arrays.asList(1l)),//
				"aid4", Maps.stringObjectMap(sfmId2, Arrays.asList(1l)),//
				"aid5", Maps.stringObjectMap(sfmId2, Arrays.asList(1l)))), month2SecondReport);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		groupId = "someGroupId";
		groupCryptoKey = Crypto.makeKey();

		sfmId1 = "sfmId1";
		sfmId2 = "sfmId2";

		email1 = "email1";
		email2 = "email2";

		userCrypto1 = Crypto.makeKey();
		userCrypto2 = Crypto.makeKey();

		user1projectCrypto = Crypto.makeKey();
		user2projectCrypto = Crypto.makeKey();

		IFunction1<String, String> repoDefnFn = Strings.firstNSegments(3);
		IUrlGenerator groupsUrlGenerator = GroupConstants.groupsGenerator(SoftwareFmConstants.urlPrefix);
		remoteGroups = new GroupsForServer(groupsUrlGenerator, remoteOperations, repoDefnFn);

		user = ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, repoDefnFn, Maps.<String,Callable<Object>>makeMap(//
				SoftwareFmConstants.projectCryptoKey, Callables.valueFromList(user1projectCrypto, user2projectCrypto), //
				GroupConstants.membershipCryptoKey, Callables.makeCryptoKey()), SoftwareFmConstants.urlPrefix);
		IFunction1<Map<String, Object>, String> userCryptoFn = Functions.mapFromKey(LoginConstants.softwareFmIdKey, sfmId1, userCrypto1, sfmId2, userCrypto2);
		IUrlGenerator userUrlGenerator = LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix);
		project = new ProjectForServer(remoteOperations, userCryptoFn, user, userUrlGenerator);
		IUsageReader usage = new UsageReaderForServer(remoteOperations, user, userUrlGenerator);
		generateUsageReportGenerator = new GenerateUsageProjectGenerator(remoteGroups, usage);

		IFunction1<String, String> emailToSoftwareFmId = Functions.map(email1, sfmId1, email2, sfmId2);
		IUserMembership membership = new UserMembershipForServer(userUrlGenerator, remoteOperations, user, userCryptoFn, repoDefnFn);
		takeOnProcessor = new TakeOnProcessor(remoteOperations, user, membership, remoteGroups, userCryptoFn, emailToSoftwareFmId, groupsUrlGenerator, Callables.value(groupId), repoDefnFn);
	}
}
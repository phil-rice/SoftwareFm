package org.softwareFm.softwareFmServer;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IGroups;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.ProjectFixture;
import org.softwareFm.eclipse.user.ProjectMock;

public class GenerateUsageProjectGeneratorTest extends GroupsTest {

	public void testGenerateReport() {
		checkMonthsReport("month1", ProjectFixture.expectedMergeResultMonth1);
		checkMonthsReport("month2", ProjectFixture.expectedMergeResultMonth2);
		checkMonthsReport("month3", ProjectFixture.expectedMergeResultMonth3);
	}

	protected void checkMonthsReport(String month, Map<String, Object> expected) {
		GenerateUsageProjectGenerator generator = new GenerateUsageProjectGenerator(//
				new GroupsForServer(GroupConstants.groupsGenerator(), remoteOperations, Strings.firstNSegments(3)), //
				new UsageReaderForServer(remoteOperations, null, userGenerator));
		Map<String, Map<String, Map<String, List<Integer>>>> month1 = generator.generateReport(groupId, groupCrypto, month);
		assertEquals(expected, month1);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		saveUserData(id1, user1Crypto, user1ProjectCrypto);
		saveUserData(id2, user2Crypto, user2ProjectCrypto);

		ProjectMock projectUser1 = ProjectFixture.project1(id1, user1ProjectCrypto);
		ProjectMock projectUser2 = ProjectFixture.project2(id2, user2ProjectCrypto);

		saveProjectData(id1, "month1", user1ProjectCrypto, projectUser1);
		saveProjectData(id1, "month2", user1ProjectCrypto, projectUser1);
		saveProjectData(id1, "month3", user1ProjectCrypto, projectUser1);

		saveProjectData(id2, "month1", user2ProjectCrypto, projectUser2);
		// saveProjectData(id2, "month2", user2ProjectCrypto, projectUser2); no month 2 data
		saveProjectData(id2, "month3", user2ProjectCrypto, projectUser2);

		IGroups groups = new GroupsForServer(groupGenerator, remoteOperations, Strings.firstNSegments(3));
		groups.setGroupProperty(groupId, groupCrypto, "someName", "someValue");
		groups.addUser(groupId, groupCrypto, Maps.stringObjectMap(SoftwareFmConstants.projectCryptoKey, user1ProjectCrypto, LoginConstants.softwareFmIdKey, id1, LoginConstants.emailKey, "email1", LoginConstants.monikerKey, "moniker1"));
		groups.addUser(groupId, groupCrypto, Maps.stringObjectMap(SoftwareFmConstants.projectCryptoKey, user2ProjectCrypto, LoginConstants.softwareFmIdKey, id2, LoginConstants.emailKey, "email2", LoginConstants.monikerKey, "moniker2"));
	}

}

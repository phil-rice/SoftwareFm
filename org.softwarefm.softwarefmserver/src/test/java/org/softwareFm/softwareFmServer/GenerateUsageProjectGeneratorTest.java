package org.softwareFm.softwareFmServer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.ProjectFixture;
import org.softwareFm.eclipse.user.ProjectMock;

public class GenerateUsageProjectGeneratorTest extends GroupsTest {

	@Test
	public void testGenerateReport() {
		checkMonthsReport("month1",//
				"group1", Maps.stringObjectMap(//
						"artifact11", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 3l, 4l, 5l), "sfm2", Arrays.asList(1l, 3l)),//
						"artifact12", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l), "sfm2", Arrays.asList(1l, 2l, 3l))),//
				"group2", Maps.stringObjectMap(//
						"artifact21", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l), "sfm2", Arrays.asList(1l, 2l)), //
						"artifact22", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l), "sfm2", Arrays.asList(1l, 2l, 3l))));

		checkMonthsReport("month2",//
				"group1", Maps.stringObjectMap(//
						"artifact11", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 3l, 4l, 5l)),//
						"artifact13", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l))),//
				"group2", Maps.stringObjectMap(//
						"artifact21", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l)), //
						"artifact22", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l))));

		checkMonthsReport("month3",//
				"group1", Maps.stringObjectMap(//
						"artifact11", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 3l, 4l, 5l), "sfm2", Arrays.asList(1l)),//
						"artifact12", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l), "sfm2", Arrays.asList(1l, 2l, 3l))),//
				"group3", Maps.stringObjectMap(//
						"artifact31", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l), "sfm2", Arrays.asList(1l, 2l)), //
						"artifact32", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l), "sfm2", Arrays.asList(4l, 5l))));
	}

	protected void checkMonthsReport(String month, Object... nameAndValues) {
		Map<String, Object> expected = Maps.makeMap(nameAndValues);
		GenerateUsageProjectGenerator generator = new GenerateUsageProjectGenerator(//
				new GroupForServer(GroupConstants.groupsGenerator(), remoteOperations, Strings.firstNSegments(3)), //
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

		IGroups groups = new GroupForServer(groupGenerator, remoteOperations, Strings.firstNSegments(3));
		groups.setGroupProperty(groupId, groupCrypto, "someName", "someValue");
		groups.addUser(groupId, groupCrypto, Maps.stringObjectMap(SoftwareFmConstants.projectCryptoKey, user1ProjectCrypto, LoginConstants.softwareFmIdKey, id1, LoginConstants.emailKey, "email1", LoginConstants.monikerKey, "moniker1"));
		groups.addUser(groupId, groupCrypto, Maps.stringObjectMap(SoftwareFmConstants.projectCryptoKey, user2ProjectCrypto, LoginConstants.softwareFmIdKey, id2, LoginConstants.emailKey, "email2", LoginConstants.monikerKey, "moniker2"));
	}

}

package org.softwareFm.eclipse.mysoftwareFm;

import java.util.Map;

import org.apache.log4j.Level;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.utilities.arrays.ArrayHelper;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.eclipse.mysoftwareFm.MyPeople.MyPeopleComposite;
import org.softwareFm.eclipse.user.ProjectFixture;
import org.softwareFm.eclipse.user.ProjectMock;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.jarAndClassPath.server.internal.GenerateGroupUsageProcessor;
import org.softwareFm.jarAndClassPath.server.internal.GenerateUsageProjectGenerator;

public class MyPeopleIntegrationTest extends AbstractMyGroupsIntegrationTest {
	private final String groupId = "group1";
	private final String artifactId = "artifact11";

	public void testContentsWhenUserNotMemberOfGroups() {
		MyPeopleComposite myPeopleComposite = displayMyPeople(groupId, artifactId, 0);

		Table table = myPeopleComposite.getEditor();
		assertEquals(0, table.getItemCount());
	}

	public void testContentsWhenMemberOfTwoGroups() {
		addUserToGroup1AndGroup2();
		addUserToGroup(softwareFmId1, email1, groupId1, groupCryptoKey1, "someStatus1");
		addUserToGroup(softwareFmId2, email2, groupId2, groupCryptoKey2, "someStatus1");

		MyPeopleComposite myPeopleComposite = displayMyPeople(groupId, artifactId, 2);//failed intermittently once

		Table table = myPeopleComposite.getEditor();
		checkTableColumns(table, "Person", "Jan 2012", "Feb 2012", "Mar 2012", "Groups");
		assertEquals(2, table.getItemCount());
		checkTable(table, 0, null, email1, "4", "4", "4", groupId1 + "Name");
		checkTable(table, 1, null, email2, "2", "", "1", groupId2 + "Name");
	}

	public void testContentsWhenAllMemberOfTwoGroups() {
		addUserToGroup1AndGroup2();

		addUserToGroup(softwareFmId1, email1, groupId1, groupCryptoKey1, "someStatus1");
		addUserToGroup(softwareFmId1, email1, groupId2, groupCryptoKey2, "someStatus2");

		addUserToGroup(softwareFmId2, email2, groupId1, groupCryptoKey1, "someStatus3");
		addUserToGroup(softwareFmId2, email2, groupId2, groupCryptoKey2, "someStatus4");

		MyPeopleComposite myPeopleComposite = displayMyPeople(groupId, artifactId, 2);

		Table table = myPeopleComposite.getEditor();
		checkTableColumns(table, "Person", "Jan 2012", "Feb 2012", "Mar 2012", "Groups");
		assertEquals(2, table.getItemCount());
		String bothGroups = groupId1 + "Name, " + groupId2 + "Name";
		checkTable(table, 0, null, email1, "4", "4", "4", bothGroups);
		checkTable(table, 1, null, email2, "2", "", "1", bothGroups);
	}

	@Override
	protected IProcessCall[] getExtraProcessCalls(IGitOperations remoteGitOperations, IFunction1<Map<String, Object>, String> cryptoFn) {
		IProcessCall[] raw = super.getExtraProcessCalls(remoteGitOperations, cryptoFn);
		ProjectMock projectMock = new ProjectMock(false);
		addProjectDetails(projectMock, softwareFmId1, userCryptoKey1, ProjectFixture.map0);
		addProjectDetails(projectMock, softwareFmId2, userCryptoKey2, ProjectFixture.map1);
		GenerateGroupUsageProcessor groupUsageProcessor = new GenerateGroupUsageProcessor(remoteGitOperations, new GenerateUsageProjectGenerator(groups, projectMock), groups);
		return ArrayHelper.append(raw, groupUsageProcessor);
	}

	protected void addProjectDetails(ProjectMock projectMock, String softwareFmId, String userCryptoKey, Map<String, Object> map) {
		String projectCrypto = user.getUserProperty(softwareFmId, userCryptoKey, JarAndPathConstants.projectCryptoKey);
		assertNotNull(projectCrypto);
		projectMock.register(softwareFmId, projectCrypto, map);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		AbstractCommandProcessor.logger.setLevel(Level.DEBUG);
		assertEquals(userCryptoKey1, signUpUser(softwareFmId1, email1));
		assertEquals(userCryptoKey2, signUpUser(softwareFmId2, email2));
	}
}

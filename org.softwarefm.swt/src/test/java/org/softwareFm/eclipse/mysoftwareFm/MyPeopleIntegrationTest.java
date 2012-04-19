/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.mysoftwareFm;

import java.util.Map;

import org.eclipse.swt.widgets.Table;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.eclipse.mysoftwareFm.MyPeople.MyPeopleComposite;
import org.softwareFm.jarAndClassPath.api.ProjectMock;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.swt.Swts;

public class MyPeopleIntegrationTest extends AbstractMyGroupsIntegrationTest {
	private final String artifactId = "artifact11";

	public void testContentsWhenMemberOfTwoGroups() {
		addUserToGroup1AndGroup2();
		addUserToGroup(softwareFmId1, email1, groupId0, groupCryptoKey0, "someStatus1");
		addUserToGroup(softwareFmId2, email2, groupId1, groupCryptoKey1, "someStatus1");

		MyPeopleComposite myPeopleComposite = displayMyPeople(groupId0, artifactId, 2);// failed intermittently once

		Table table = myPeopleComposite.getEditor();
		Swts.checkTableColumns(table, "Person", "Jan 2012", "Feb 2012", "Mar 2012", "Groups");
		assertEquals(2, table.getItemCount());
		Swts.checkTable(table, 0, null, email0, "4", "4", "4", groupName0 + ", " + groupName1);
		Swts.checkTable(table, 1, null, email1, "2", "", "1", groupName0);
	}

	public void testContentsWhenUserNotMemberOfGroups() {
		MyPeopleComposite myPeopleComposite = displayMyPeople(groupId0, artifactId, 0);

		Table table = myPeopleComposite.getEditor();
		assertEquals(0, table.getItemCount());
	}

	public void testContentsWhenAllMemberOfTwoGroups() {
		addUserToGroup1AndGroup2();

		addUserToGroup(softwareFmId1, email1, groupId0, groupCryptoKey0, "someStatus1");
		addUserToGroup(softwareFmId1, email1, groupId1, groupCryptoKey1, "someStatus2");

		addUserToGroup(softwareFmId2, email2, groupId0, groupCryptoKey0, "someStatus3");
		addUserToGroup(softwareFmId2, email2, groupId1, groupCryptoKey1, "someStatus4");

		MyPeopleComposite myPeopleComposite = displayMyPeople(groupId0, artifactId, 2);

		Table table = myPeopleComposite.getEditor();
		Swts.checkTableColumns(table, "Person", "Jan 2012", "Feb 2012", "Mar 2012", "Groups");
		assertEquals(2, table.getItemCount());
		String bothGroups = groupId0 + "Name, " + groupId1 + "Name";
		Swts.checkTable(table, 0, null, email0, "4", "4", "4", bothGroups);
		Swts.checkTable(table, 1, null, email1, "2", "", "1", bothGroups);
	}

	protected void addProjectDetails(ProjectMock projectMock, String softwareFmId, String userCryptoKey, Map<String, Object> map) {
		String projectCrypto = IUserReader.Utils.getUserProperty(getLocalApi().makeUserAndGroupsContainer(), softwareFmId, userCryptoKey, JarAndPathConstants.projectCryptoKey);
		assertNotNull(projectCrypto);
		projectMock.register(softwareFmId, projectCrypto, map);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// AbstractCallProcessor.logger.setLevel(Level.DEBUG);
		createUser(softwareFmId1, email1);
		createUser(softwareFmId2, email2);
	}
}
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IGroups;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.LocalGroupsReader;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;

public class GroupsForServerTest extends GroupsTest {

	public IFunction1<String, String> repoGenerator = Strings.firstNSegments(3);

	public void testAddGetPropertyWithoutUsers() {
		checkSetGetGroups();
	}
	
	

	public void testAddUsers() {
		IGroups groups = new GroupsForServer(groupGenerator, remoteOperations, repoGenerator);
		groups.setGroupProperty(groupId, groupCrypto, "someProperty", "someValue");
		groups.addUser(groupId, groupCrypto, makeUserDetails(0));
		groups.addUser(groupId, groupCrypto, makeUserDetails(1));
		groups.addUser(groupId, groupCrypto, makeUserDetails(2));

		checkUsers(3);
	}

	public void testAddingUsersDoesntImpactOnProperties() {
		IGroups groups = new GroupsForServer(groupGenerator, remoteOperations, repoGenerator);
		checkSetGetGroups();
		groups.addUser(groupId, groupCrypto, makeUserDetails(0));
		checkUsers(1);

		checkGetNewGroups();
		groups.addUser(groupId, groupCrypto, makeUserDetails(1));
		checkUsers(2);

		checkGetNewGroups();
		groups.addUser(groupId, groupCrypto, makeUserDetails(2));
		checkUsers(3);
	}

	public void testSettingPropertiesDoesntImpactOnUsers() {
		IGroups groups = new GroupsForServer(groupGenerator, remoteOperations, repoGenerator);
		IGroups groups1 = new GroupsForServer(groupGenerator, remoteOperations, repoGenerator);
		IGroupsReader localGroupsReader = new LocalGroupsReader(groupGenerator, gitLocal);

		groups1.setGroupProperty(groupId, groupCrypto, "property1", "value1");
		groups1.setGroupProperty(groupId, groupCrypto, "property2", "value2");
		groups1.setGroupProperty(groupId, groupCrypto, "property3", "value3");

		assertEquals("value1", groups1.getGroupProperty(groupId, groupCrypto, "property1"));
		assertEquals("value2", groups1.getGroupProperty(groupId, groupCrypto, "property2"));
		assertEquals("value3", groups1.getGroupProperty(groupId, groupCrypto, "property3"));

		assertEquals("value1", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property1"));
		assertEquals("value2", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));
		assertEquals("value3", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property3"));

		groups1.setGroupProperty(groupId, groupCrypto, "property2", "value2a");
		assertEquals("value2", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));
		localGroupsReader.refresh(groupId);
		assertEquals("value2a", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));

		checkGetNewGroups();
		groups.addUser(groupId, groupCrypto, makeUserDetails(0));
		checkUsers(1);

		checkGetNewGroups();
		groups.addUser(groupId, groupCrypto, makeUserDetails(1));
		checkUsers(2);

		checkGetNewGroups();
		groups.addUser(groupId, groupCrypto, makeUserDetails(2));
		checkUsers(3);
	}

	public void testSetGetReport() {
		IGroups groups = new GroupsForServer(groupGenerator, remoteOperations, repoGenerator);
		groups.setReport(groupId, groupCrypto, "month1", v11);
		groups.setReport(groupId, groupCrypto, "month2", v12);
		assertEquals(v11, groups.getUsageReport(groupId, groupCrypto, "month1"));
		assertEquals(v12, groups.getUsageReport(groupId, groupCrypto, "month2"));
		assertNull(groups.getUsageReport(groupId, groupCrypto, "noSuchMonth"));

		IGroupsReader localGroupsReader = new LocalGroupsReader(groupGenerator, gitLocal);
		assertEquals(v11, localGroupsReader.getUsageReport(groupId, groupCrypto, "month1"));
		assertEquals(v12, localGroupsReader.getUsageReport(groupId, groupCrypto, "month2"));
		
	}

	private void checkUsers(int userCount) {
		IGroups groups = new GroupsForServer(groupGenerator, remoteOperations, repoGenerator);
		List<Map<String, Object>> expected = Lists.newList();
		for (int i = 0; i < userCount; i++)
			expected.add(makeUserDetails(i));
		assertEquals(expected, Iterables.list(groups.users(groupId, groupCrypto)));

		IGroupsReader localGroupsReader = new LocalGroupsReader(groupGenerator, gitLocal);
		localGroupsReader.refresh("");
		assertEquals(expected, Iterables.list(localGroupsReader.users(groupId, groupCrypto)));
	}

	private Map<String, Object> makeUserDetails(long i) {
		return Maps.stringObjectMap("value", i);
	}

	protected void checkSetGetGroups() {
		IGroups groups = new GroupsForServer(groupGenerator, remoteOperations, repoGenerator);
		IGroupsReader localGroupsReader = new LocalGroupsReader(groupGenerator, gitLocal);

		groups.setGroupProperty(groupId, groupCrypto, "property1", "value1");
		groups.setGroupProperty(groupId, groupCrypto, "property2", "value2");
		groups.setGroupProperty(groupId, groupCrypto, "property3", "value3");

		assertEquals("value1", groups.getGroupProperty(groupId, groupCrypto, "property1"));
		assertEquals("value2", groups.getGroupProperty(groupId, groupCrypto, "property2"));
		assertEquals("value3", groups.getGroupProperty(groupId, groupCrypto, "property3"));

		assertEquals("value1", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property1"));
		assertEquals("value2", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));
		assertEquals("value3", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property3"));

		groups.setGroupProperty(groupId, groupCrypto, "property2", "value2a");
		assertEquals("value2", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));
		localGroupsReader.refresh(groupId);
		assertEquals("value2a", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));

		checkGetNewGroups();
	}

	protected void checkGetNewGroups() {
		IGroups groups = new GroupsForServer(groupGenerator, remoteOperations, repoGenerator);
		IGroupsReader localGroupsReader = new LocalGroupsReader(groupGenerator, gitLocal);

		assertEquals("value1", groups.getGroupProperty(groupId, groupCrypto, "property1"));
		assertEquals("value2a", groups.getGroupProperty(groupId, groupCrypto, "property2"));
		assertEquals("value3", groups.getGroupProperty(groupId, groupCrypto, "property3"));

		assertEquals("value1", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property1"));
		assertEquals("value2a", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));
		assertEquals("value3", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property3"));
	}

}
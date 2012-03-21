/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.server.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.jarAndClassPath.api.GroupsTest;
import org.softwareFm.jarAndClassPath.api.IGenerateUsageReportGenerator;
import org.softwareFm.jarAndClassPath.api.ProjectFixture;
import org.softwareFm.jarAndClassPath.api.ProjectMock;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;

public class GenerateUsageProjectGeneratorTest extends GroupsTest {

	public void testGenerateReport() {
		checkMonthsReport("january_12", ProjectFixture.expectedMergeResultMonth1);
		checkMonthsReport("febuary_12", ProjectFixture.expectedMergeResultMonth2);
		checkMonthsReport("march_12", ProjectFixture.expectedMergeResultMonth3);
	}

	protected void checkMonthsReport(final String month, final Map<String, Object> expected) {
		readWriteApi.modify(IGenerateUsageReportGenerator.class, new ICallback<IGenerateUsageReportGenerator>() {
			@Override
			public void process(IGenerateUsageReportGenerator generator) throws Exception {
				Map<String, Map<String, Map<String, List<Integer>>>> month1 = generator.generateReport(groupId, groupCrypto, month);
				assertEquals(expected, month1);

			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final String user1ProjectCrypto = IUserReader.Utils.getUserProperty(readWriteApi, sfmId1, userKey0, JarAndPathConstants.projectCryptoKey);
		final String user2ProjectCrypto = IUserReader.Utils.getUserProperty(readWriteApi, sfmId2, userKey1, JarAndPathConstants.projectCryptoKey);

		ProjectMock projectUser1 = ProjectFixture.project1(sfmId1, user1ProjectCrypto);
		ProjectMock projectUser2 = ProjectFixture.project2(sfmId2, user2ProjectCrypto);

		saveProjectData(sfmId1, user1ProjectCrypto, "january_12", projectUser1);
		saveProjectData(sfmId1, user1ProjectCrypto, "febuary_12", projectUser1);
		saveProjectData(sfmId1, user1ProjectCrypto, "march_12", projectUser1);

		saveProjectData(sfmId2, user2ProjectCrypto, "january_12", projectUser2);
		// saveProjectData(id2, "month2", user2ProjectCrypto, projectUser2); no month 2 data
		saveProjectData(sfmId2, user2ProjectCrypto, "march_12", projectUser2);
		readWriteApi.modifyGroups(new ICallback<IGroups>() {
			@Override
			public void process(IGroups groups) throws Exception {
				groups.setGroupProperty(groupId, groupCrypto, "someName", "someValue");
				groups.addUser(groupId, groupCrypto, Maps.stringObjectMap(JarAndPathConstants.projectCryptoKey, user1ProjectCrypto, LoginConstants.softwareFmIdKey, sfmId1, LoginConstants.emailKey, "email1", LoginConstants.monikerKey, "moniker1"));
				groups.addUser(groupId, groupCrypto, Maps.stringObjectMap(JarAndPathConstants.projectCryptoKey, user2ProjectCrypto, LoginConstants.softwareFmIdKey, sfmId2, LoginConstants.emailKey, "email2", LoginConstants.monikerKey, "moniker2"));
			}
		});
	}

}
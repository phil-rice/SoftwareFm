/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.server.internal;

import java.util.Arrays;
import java.util.Map;

import org.softwareFm.crowdsource.api.server.ITakeOnProcessor;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.jarAndClassPath.api.GroupsTest;
import org.softwareFm.jarAndClassPath.api.IProject;

public class GenerateGroupUsageProcessorTest extends GroupsTest {

	private final String groupCryptoKey = Crypto.makeKey();
	private ITakeOnProcessor takeOnProcessor;

	public void test() {
		final String groupId = takeOnProcessor.createGroup("someGroupName", groupCryptoKey);
		takeOnProcessor.addExistingUserToGroup(groupId, groupCryptoKey, sfmId1, "email1", "someStatus");
		takeOnProcessor.addExistingUserToGroup(groupId, groupCryptoKey, sfmId2, "email2", "someStatus");

		serverContainer.access(IProject.class, new ICallback<IProject>() {
			@Override
			public void process(IProject project) throws Exception {
				project.addProjectDetails(sfmId1, "gid1", "aid1", "month1", 1);
				project.addProjectDetails(sfmId1, "gid1", "aid1", "month1", 2);
				project.addProjectDetails(sfmId1, "gid1", "aid2", "month2", 1);
				project.addProjectDetails(sfmId1, "gid1", "aid3", "month2", 1);

				project.addProjectDetails(sfmId2, "gid1", "aid1", "month1", 1);
				project.addProjectDetails(sfmId2, "gid1", "aid2", "month1", 2);
				project.addProjectDetails(sfmId2, "gid1", "aid4", "month2", 1);
				project.addProjectDetails(sfmId2, "gid1", "aid5", "month2", 1);
			}
		}).get();

		serverContainer.accessGroups(new ICallback<IGroups>() {
			@Override
			public void process(IGroups groups) throws Exception {
				GenerateGroupUsageProcessor processor = new GenerateGroupUsageProcessor(serverContainer);
				processor.execute(GroupConstants.generateGroupReportPrefix, Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.monthKey, "month1", GroupConstants.groupCryptoKey, groupCryptoKey));
				Map<String, Object> month1Report = groups.getUsageReport(groupId, groupCryptoKey, "month1");
				Map<String, Object> month2Report = groups.getUsageReport(groupId, groupCryptoKey, "month2");
				assertEquals(Maps.stringObjectMap("gid1", Maps.stringObjectMap(//
						"aid1", Maps.stringObjectMap(sfmId1, Arrays.asList(1l, 2l), sfmId2, Arrays.asList(1l)),//
						"aid2", Maps.stringObjectMap(sfmId2, Arrays.asList(2l)))), month1Report);
				assertEquals(null, month2Report);

				processor.execute(GroupConstants.generateGroupReportPrefix, Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.monthKey, "month2", GroupConstants.groupCryptoKey, groupCryptoKey));
				Map<String, Object> month2SecondReport = groups.getUsageReport(groupId, groupCryptoKey, "month2");
				assertEquals(Maps.stringObjectMap("gid1", Maps.stringObjectMap(//
						"aid2", Maps.stringObjectMap(sfmId1, Arrays.asList(1l)), //
						"aid3", Maps.stringObjectMap(sfmId1, Arrays.asList(1l)),//
						"aid4", Maps.stringObjectMap(sfmId2, Arrays.asList(1l)),//
						"aid5", Maps.stringObjectMap(sfmId2, Arrays.asList(1l)))), month2SecondReport);
			}
		}).get();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		takeOnProcessor = getServerDoers().getTakeOnProcessor();
	}
}
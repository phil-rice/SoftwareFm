/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.server.internal;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonMessages;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.jarAndClassPath.api.IGenerateUsageReportGenerator;
import org.softwareFm.jarAndClassPath.api.IUsageReader;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;

public class GenerateUsageProjectGenerator implements IGenerateUsageReportGenerator {

	private final Logger logger = Logger.getLogger(GenerateUsageProjectGenerator.class);
	private final IContainer container;
	private final long timeOutMs;

	public GenerateUsageProjectGenerator(IContainer container, long timeOutMs) {
		this.container = container;
		this.timeOutMs = timeOutMs;
	}

	@Override
	public Map<String, Map<String, Map<String, List<Integer>>>> generateReport(final String groupId, final String groupCryptoKey, final String month) {
		return container.accessWithCallback(IGroupsReader.class, IUsageReader.class, new IFunction2<IGroupsReader, IUsageReader, Map<String, Map<String, Map<String, List<Integer>>>>>() {
			@Override
			public Map<String, Map<String, Map<String, List<Integer>>>> apply(final IGroupsReader groupsReader, IUsageReader usageReader) throws Exception {
				final Map<String, Map<String, Map<String, List<Integer>>>> result = Maps.newMap();
				logger.debug("generateReport. GroupId: " + groupId + ", Month: " + month);
				Iterable<Map<String, Object>> users = groupsReader.users(groupId, groupCryptoKey);
				for (Map<String, Object> userData : users) {
					final String usersProjectCryptoKey = (String) userData.get(JarAndPathConstants.projectCryptoKey);
					if (usersProjectCryptoKey == null)
						throw new IllegalStateException(MessageFormat.format(CommonMessages.userProjectKeyNotPresent, userData));
					final String softwareFmId = (String) userData.get(LoginConstants.softwareFmIdKey);
					logger.debug("user: " + softwareFmId);
					Map<String, Map<String, List<Integer>>> projectDetails = usageReader.getProjectDetails(softwareFmId, usersProjectCryptoKey, month);
					logger.debug("ProjectDetails: \n" + projectDetails);
					if (projectDetails != null)
						for (Entry<String, Map<String, List<Integer>>> groupEntry : projectDetails.entrySet())
							for (Entry<String, List<Integer>> artifactEntry : groupEntry.getValue().entrySet())
								Maps.addToMapOfMapOfMaps(result, HashMap.class, groupEntry.getKey(), artifactEntry.getKey(), softwareFmId, artifactEntry.getValue());
				}
				return result;
			}
		}, ICallback.Utils.<Map<String, Map<String, Map<String, List<Integer>>>>> noCallback()).get(timeOutMs);
	}
}
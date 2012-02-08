package org.softwareFm.softwareFmServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IUsageReader;
import org.softwareFm.server.processors.IGenerateUsageReportGenerator;

public class GenerateUsageProjectGenerator implements IGenerateUsageReportGenerator {

	private final IGroupsReader groupsReader;
	private final IUsageReader usageReader;

	public GenerateUsageProjectGenerator(IGroupsReader groupsReader, IUsageReader usageReader) {
		this.groupsReader = groupsReader;
		this.usageReader = usageReader;
	}

	@Override
	public Map<String, Map<String, Map<String, List<Integer>>>> generateReport(String groupId, String groupCryptoKey, String month) {
		Map<String, Map<String, Map<String, List<Integer>>>> result = Maps.newMap();
		for (Map<String, Object> userData : groupsReader.users(groupId, groupCryptoKey)) {
			String usersProjectCryptoKey = (String) userData.get(SoftwareFmConstants.projectCryptoKey);
			String softwareFmId = (String) userData.get(LoginConstants.softwareFmIdKey);
			Map<String, Map<String, List<Integer>>> projectDetails = usageReader.getProjectDetails(softwareFmId, usersProjectCryptoKey, month);
			if (projectDetails != null)
				for (Entry<String, Map<String, List<Integer>>> groupEntry : projectDetails.entrySet())
					for (Entry<String, List<Integer>> artifactEntry : groupEntry.getValue().entrySet())
						Maps.addToMapOfMapOfMaps(result, HashMap.class, groupEntry.getKey(), artifactEntry.getKey(), softwareFmId, artifactEntry.getValue());
		}
		return result;
	}

}

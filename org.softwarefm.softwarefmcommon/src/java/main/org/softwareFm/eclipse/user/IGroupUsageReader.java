package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IGroupsReader;

public interface IGroupUsageReader extends IGroupsReader {
	/** group -> artifact -> person -> list of the days */
	Map<String, Map<String, Map<String, List<Integer>>>> getProjectDetails(String groupId, String groupCryptoKey, String month);
}

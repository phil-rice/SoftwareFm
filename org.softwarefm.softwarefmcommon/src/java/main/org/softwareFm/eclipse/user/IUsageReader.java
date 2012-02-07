package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;

public interface IUsageReader {
	/** group -> artifact -> list of the days */
	Map<String, Map<String, List<Integer>>> getProjectDetails(String softwareFmId, String usersProjectCryptoKey, String month);

}

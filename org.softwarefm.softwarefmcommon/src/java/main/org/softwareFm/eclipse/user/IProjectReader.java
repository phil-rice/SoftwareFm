package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;

public interface IProjectReader {
	/** group -> artifact -> list of the days */
	Map<String, Map<String, List<Integer>>> getProjectDetails(Map<String, Object> userDetailMap, String month);

}

package org.softwareFm.server.user;

import java.util.Map;

public interface IProject {

	void saveProjectDetails(Map<String, Object> userDetailMap, String month, Map<String, Object> data);

	Map<String, Object> getProjectDetails(Map<String, Object> userDetailMap, String month);

	Map<String, Object> addProjectDetails(Map<String, Object> userAndProjectDetailMap, String month, long day, Map<String, Object> initialProjectDetails);

}

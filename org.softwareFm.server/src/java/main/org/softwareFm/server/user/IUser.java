package org.softwareFm.server.user;

import java.util.Map;

import org.softwareFm.server.ILocalGitClient;
import org.softwareFm.server.internal.User;
import org.softwareFm.utilities.url.IUrlGenerator;

public interface IUser {

	Map<String, Object> getUserDetails(Map<String, Object> userDetails);

	void saveUserDetails(Map<String, Object> userDetails, Map<String, Object> data);

	void saveProjectDetails(Map<String, Object> userDetails, String month, Map<String, Object> data);

	Map<String, Object> getProjectDetails(Map<String, Object> userDetails, String month);

	Map<String, Object> addProjectDetails(Map<String, Object> userAndProjectDetails, String month, long day, Map<String, Object> data);


	public static class Utils {
		public static IUser makeUserDetails(ILocalGitClient gitClient, IUrlGenerator userGenerator, IUrlGenerator projectDetailGenerator, String groupIdKey, String artifactIdKey) {
			return new User(gitClient, userGenerator, projectDetailGenerator, groupIdKey, artifactIdKey);

		}
	}

}

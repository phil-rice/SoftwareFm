package org.softwareFm.server.user;

import java.util.Map;

import org.softwareFm.server.ILocalGitClient;
import org.softwareFm.server.user.internal.User;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.url.IUrlGenerator;

public interface IUser {

	Map<String, Object> getUserDetails(Map<String, Object> userDetailMap);

	void saveUserDetails(Map<String, Object> userDetailMap, Map<String, Object> data);

	void saveProjectDetails(Map<String, Object> userDetailMap, String month, Map<String, Object> data);

	Map<String, Object> getProjectDetails(Map<String, Object> userDetailMap, String month);

	Map<String, Object> addProjectDetails(Map<String, Object> userAndProjectDetailMap, String month, long day, Map<String, Object> initialProjectDetails);

	public static class Utils {
		public static IUser makeUserDetails(ILocalGitClient gitClient, IUrlGenerator userGenerator, IUrlGenerator projectDetailGenerator, IFunction1<Map<String, Object>, String> cryptoFn, String groupIdKey, String artifactIdKey) {
			return new User(gitClient, userGenerator, projectDetailGenerator, cryptoFn, groupIdKey, artifactIdKey);

		}
	}

}

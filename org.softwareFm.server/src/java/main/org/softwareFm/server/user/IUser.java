package org.softwareFm.server.user;

import java.util.Map;

import org.softwareFm.server.user.internal.User;
import org.softwareFm.server.user.internal.UserMock;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.url.IUrlGenerator;

public interface IUser {

	Map<String, Object> getUserDetails(Map<String, Object> userDetailMap);

	void saveUserDetails(Map<String, Object> userDetailMap, Map<String, Object> data);

	public static class Utils {
		public static IUser makeUserDetails(ILocalGitClient gitClient, IUrlGenerator userGenerator, IUrlGenerator projectDetailGenerator, IFunction1<Map<String, Object>, String> cryptoFn, String groupIdKey, String artifactIdKey) {
			return new User(gitClient, userGenerator, projectDetailGenerator, cryptoFn, groupIdKey, artifactIdKey);

		}

		public static UserMock userMock() {
			return new UserMock();
		}

		public static IUser noUserDetails() {
			return new IUser() {
				@Override
				public void saveUserDetails(Map<String, Object> userDetailMap, Map<String, Object> data) {
					throw new UnsupportedOperationException();
				}

				@Override
				public Map<String, Object> getUserDetails(Map<String, Object> userDetailMap) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

}

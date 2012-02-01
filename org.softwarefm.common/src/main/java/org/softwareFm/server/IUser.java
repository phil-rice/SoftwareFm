package org.softwareFm.server;

import java.util.Map;

import org.softwareFm.server.internal.ServerUser;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.url.IUrlGenerator;

public interface IUser extends IUserReader {

	<T>void setUserProperty(Map<String, Object> userDetails, String cryptoKey, String property, T value);

	public static class Utils {

		public static IUser makeUserForServer(IGitOperations gitOperations, IUrlGenerator userUrlGenerator, IFunction1<String, String> findRepositoryRoot){
			return new ServerUser(gitOperations, userUrlGenerator, findRepositoryRoot);
		}
	
	}

}

package org.softwareFm.common;

import java.util.Map;

import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.internal.ServerUser;
import org.softwareFm.common.url.IUrlGenerator;

public interface IUser extends IUserReader {

	<T>void setUserProperty(Map<String, Object> userDetails, String cryptoKey, String property, T value);

	abstract public static class Utils {

		public static IUser makeUserForServer(IGitOperations gitOperations, IUrlGenerator userUrlGenerator, IFunction1<String, String> findRepositoryRoot){
			return new ServerUser(gitOperations, userUrlGenerator, findRepositoryRoot);
		}
	
	}

}

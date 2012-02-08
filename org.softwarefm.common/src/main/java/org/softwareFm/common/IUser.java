package org.softwareFm.common;


public interface IUser extends IUserReader {

	<T>void setUserProperty(String userId, String cryptoKey, String property, T value);

	

}

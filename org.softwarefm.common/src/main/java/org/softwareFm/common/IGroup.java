package org.softwareFm.common;


public interface IGroup extends IGroupReader{
	void getUserProperty(String groupId, String projectCryptoKey, String propertyName, String value);
	
	
}

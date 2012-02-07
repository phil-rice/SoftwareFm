package org.softwareFm.common;


public interface IGroups extends IGroupsReader{
	void getGroupProperty(String groupId, String projectCryptoKey, String propertyName, String value);
	
	
}

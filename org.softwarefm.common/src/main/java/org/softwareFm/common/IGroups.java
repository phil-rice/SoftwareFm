package org.softwareFm.common;

import java.util.Map;

public interface IGroups extends IGroupsReader {

	void setGroupProperty(String groupId, String groupCryptoKey, String property, String value);

	void addUser(String groupId, String groupCryptoKey, Map<String, Object> userDetails);

	void setReport(String groupId, String groupCryptoKey, String month, Map<String, Object> report);

}

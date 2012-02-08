package org.softwareFm.common;

import java.util.Map;

/**
 * This is the reader for information about groups of people
 * 
 * The group file is a set of lines, each line encrypted with the groupCryptoKey. If you are a member of the group, you have the key. This is done so that changes to the group data are "git friendly". Adding another user required another line, changing a users or the group's properties is again only one line. This means that git can send deltas for only the line
 * 
 * The first line is the encrypted value of a map of group properties
 * 
 * Each line after the first is data for a user *
 * 
 * @author Phil
 * 
 */
public interface IGroupsReader {

	<T>T getGroupProperty(String groupId, String groupCryptoKey, String propertyName);

	/** Iterates through the users one at a time. The map is of the form {softwareFmId -> "someSoftwareFmId", email -> "someEmail", "moniker"->"someMoniker", status -> "admin/member/invited/requesting"}. Email or monikor are optional */
	Iterable<Map<String, Object>> users(String groupId, String groupCryptoKey);

	/** pulls the latest data about the group from the server */
	void refresh(String groupId);
	
	Map<String,Object> getUsageReport(String groupId, String groupCryptoKey, String month);

}

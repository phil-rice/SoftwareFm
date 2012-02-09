package org.softwareFm.softwareFmServer;

public interface ITakeOnProcessor {

	/** returns the new group id */
	String createGroup(String groupName, String groupCrypto);
	
	/** Adds the user to the group, and updates the group with the users data. */
	void addExistingUserToGroup(String groupId, String groupName, String groupCryptoKey, String email);

}

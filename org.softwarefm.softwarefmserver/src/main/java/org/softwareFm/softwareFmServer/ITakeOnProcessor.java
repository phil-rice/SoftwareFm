package org.softwareFm.softwareFmServer;

public interface ITakeOnProcessor {

	/** creates the user, and returns the magic string that needs to be emailed / clicked on to get a password */
	String createUser(String email);

	/** Adds the user to the group, and updates the group with the users data. */
	void addExistingUserToGroup(String groupId, String groupName, String email);

}

package org.softwarefm.shared.social;

import java.util.List;

import org.softwarefm.shared.usage.IUsageStats;

public interface ISocial {

	/** May return null if not logged in, or unknown. */
	String myName();

	/** Will return empty list if not logged in, or unknown */
	List<FriendData> myFriends();

	/** Will return empty IUsageStates if don't know them for that person */
	IUsageStats getUsageStats(String name);

	List<String> names();
}

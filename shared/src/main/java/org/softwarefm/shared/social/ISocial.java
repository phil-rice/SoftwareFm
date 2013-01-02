package org.softwarefm.shared.social;

import java.util.List;

import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.maps.ISimpleMap;

//TODO This is a little awkward, because the usage stats include version data in the url, but we want to compare against the artifact usually. At the moment there are separate methods, but we probably need to clean this up...   
public interface ISocial {

	/** May return null if not logged in, or unknown. */
	String myName();

	/** Will return empty list if not logged in, or unknown */
	List<FriendData> myFriends();

	IUsageStats getUsageStats(String name);

	UsageStatData getUsageStatsForCode(String name, String url);

	UsageStatData getUsageStatsForArtifact(String name, String projectUrl);

	ISimpleMap<FriendData, UsageStatData> getFriendsCodeUsage(String url);

	ISimpleMap<FriendData, UsageStatData> getFriendsArtifactUsage(String projectUrl);

	List<String> names();
}

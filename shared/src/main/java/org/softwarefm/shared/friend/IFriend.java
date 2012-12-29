package org.softwarefm.shared.friend;

import java.util.List;

import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.maps.ISimpleMap;

public interface IFriend {

	List<String> friendNames(String user);

	ISimpleMap<String, IUsageStats> friendsUsage(String user);

	ISimpleMap<String, UsageStatData> pathToFriendsUsage(String user, String path);

}

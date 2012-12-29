package org.softwarefm.core.friends;

import java.util.List;

import org.softwarefm.shared.friend.IFriend;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.maps.ISimpleMap;

public class FriendManagerLocal implements IFriend {

	@Override
	public List<String> friendNames(String user) {
		return null;
	}

	@Override
	public ISimpleMap<String, IUsageStats> friendsUsage(String user) {
		return null;
	}

	@Override
	public ISimpleMap<String, UsageStatData> pathToFriendsUsage(String user, String path) {
		return null;
	}

}

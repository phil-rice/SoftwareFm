package org.softwarefm.core.friends;

import java.util.List;

import org.softwarefm.core.client.IUserConnectionDetails;
import org.softwarefm.shared.friend.IFriends;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.maps.ISimpleMap;

public class FriendLocal implements IFriends {

	
	private  IUserConnectionDetails userConnectionDetails;

	public void setUserConnectionDetails(IUserConnectionDetails userConnectionDetails) {
		this.userConnectionDetails = userConnectionDetails;
	}

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

package org.softwarefm.shared.friend;

import java.util.List;

import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.maps.ISimpleMap;

public interface IFriend {

	List<String> friendNames(String user);

	ISimpleMap<String, IUsageStats> friendsUsage(String user);

	IUsageStats path(String user, String path);

}

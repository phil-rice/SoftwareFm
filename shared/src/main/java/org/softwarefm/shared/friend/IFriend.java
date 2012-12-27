package org.softwarefm.shared.friend;

import java.util.List;

import org.softwarefm.eclipse.usage.UsageStatData;
import org.softwarefm.utilities.maps.ISimpleMap;

public interface IFriend {

	List<String> friendNames(String user);

	ISimpleMap<String, ISimpleMap<String, UsageStatData>> friendsUsage(String user);

	ISimpleMap<String, UsageStatData> path(String user, String path);

}

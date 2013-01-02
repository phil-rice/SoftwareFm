package org.softwarefm.core.friends;

import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.maps.ISimpleMap;

public interface ISocialUsageListener {

	void codeUsage(String url, UsageStatData myUsage, ISimpleMap<FriendData, UsageStatData> friendsUsage);

	void artifactUsage(String url, UsageStatData myUsage, ISimpleMap<FriendData, UsageStatData> friendsUsage);

	void noArtifactUsage();

}

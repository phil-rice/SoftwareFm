package org.softwarefm.shared.social;

import java.util.List;

import org.softwarefm.shared.social.internal.SocialManager;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.events.IMultipleListenerList;

public interface ISocialManager extends ISocial {

	void setMyName(String name);

	void setFriendsData(List<FriendData> friendsData);

	void addFoundNameListener(IFoundNameListener listener);

	void removeFoundNameListener(IFoundNameListener listener);

	void addFoundFriendsListener(IFoundFriendsListener listener);

	void removeFoundFriendsListener(IFoundFriendsListener listener);

	void clearUsageData();

	void setUsageData(String name, IUsageStats stats);

	String serialize();

	void populate(String serializedForm);

	public static class Utils {

		public static ISocialManager socialManager(IMultipleListenerList listenerList, IUsagePersistance persistance) {
			return new SocialManager(listenerList, persistance);
		}
	}

}

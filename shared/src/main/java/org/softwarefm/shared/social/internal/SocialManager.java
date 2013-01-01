package org.softwarefm.shared.social.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.social.IFoundFriendsListener;
import org.softwarefm.shared.social.IFoundNameListener;
import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.strings.Strings;

public class SocialManager implements ISocialManager {

	private final IMultipleListenerList listenerList;
	private List<FriendData> friends = Collections.emptyList();
	private String name;
	private final Map<String, IUsageStats> stats = new HashMap<String, IUsageStats>();
	private final IUsagePersistance persistance;

	public SocialManager(IMultipleListenerList listenerList, IUsagePersistance persistance) {
		super();
		this.listenerList = listenerList;
		this.persistance = persistance;
	}

	@Override
	public String myName() {
		return name;
	}

	@Override
	public List<FriendData> myFriends() {
		return friends;
	}

	@Override
	public void setMyName(final String name) {
		if (Strings.safeEquals(name, this.name))
			return;
		this.name = name;
		listenerList.fire(this, IFoundNameListener.class, new ICallback<IFoundNameListener>() {
			@Override
			public void process(IFoundNameListener t) throws Exception {
				t.foundName(name);
			}
		});
	}

	@Override
	public void setFriendsData(List<FriendData> friendsData) {
		if (friendsData.equals(this.friends))
			return;
		this.friends = Collections.unmodifiableList(new ArrayList<FriendData>(friendsData));
		listenerList.fire(this, IFoundFriendsListener.class, new ICallback<IFoundFriendsListener>() {
			@Override
			public void process(IFoundFriendsListener t) throws Exception {
				t.foundFriends(SocialManager.this.friends);
			}
		});
	}

	@Override
	public IUsageStats getUsageStats(String name) {
		IUsageStats result = stats.get(name);
		if (result == null)
			return IUsageStats.Utils.empty();
		else
			return result;
	}

	@Override
	public void clearUsageData() {
		stats.clear();
	}

	@Override
	public void setUsageData(String name, IUsageStats stats) {
		this.stats.put(name, stats);
	}

	@Override
	public void addFoundFriendsListener(IFoundFriendsListener listener) {
		listenerList.addListener(this, IFoundFriendsListener.class, listener);

	}

	@Override
	public void removeFoundFriendsListener(IFoundFriendsListener listener) {
		listenerList.removeListener(this, IFoundFriendsListener.class, listener);
	}

	@Override
	public void addFoundNameListener(IFoundNameListener listener) {
		listenerList.addListener(this, IFoundNameListener.class, listener);

	}

	@Override
	public void removeFoundNameListener(IFoundNameListener listener) {
		listenerList.removeListener(this, IFoundNameListener.class, listener);
	}

	@Override
	public String serialize() {
		return persistance.save(this);
	}

	@Override
	public void populate(String serializedForm) {
		persistance.populate(this, serializedForm);
	}

	@Override
	public List<String> names() {
		return new ArrayList<String>(stats.keySet());
	}
}

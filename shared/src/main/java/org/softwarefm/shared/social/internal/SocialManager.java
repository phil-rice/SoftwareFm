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
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.maps.Maps;
import org.softwarefm.utilities.maps.SimpleMaps;
import org.softwarefm.utilities.strings.Strings;

public class SocialManager implements ISocialManager {
	public static final UsageStatData empty = new UsageStatData(0);

	private final IMultipleListenerList listenerList;
	private List<FriendData> friends = Collections.emptyList();
	private String name;
	private final Map<String, IUsageStats> stats = new HashMap<String, IUsageStats>();
	private final IUsagePersistance persistance;
	// TODO ok this is a bodge...I need to restructure the data to fix it. At the moment usage is recorded url->usage, but actually the url has meaning, and I need to use it to hide version data
	/** artifactUrl -> name -> usage */
	private final Map<String, Map<String, Integer>> artifactStats = new HashMap<String, Map<String, Integer>>();

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
		if (name == null || Strings.safeEquals(name, this.name))
			return;
		setMyNameInternal(name);
	}

	private void setMyNameInternal(final String name) {
		this.name = name;
		listenerList.fire(this, IFoundNameListener.class, new ICallback<IFoundNameListener>() {
			@Override
			public void process(IFoundNameListener t) throws Exception {
				t.foundName(name);
			}
		});
	}

	@Override
	public void clearMyName() {
		setMyNameInternal(null);
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
		artifactStats.clear();
	}

	@Override
	public void setUsageData(String name, IUsageStats stats) {
		if (name != null) {
			this.stats.put(name, stats);
			for (int i = 0; i < stats.size(); i++) {
				String url = stats.key(i); // see todo at the top...yes this is nasty, buggy and knows too much about structure
				if (url.startsWith("artifact")) {
					String projectUrl = Strings.allButLastSegment(url, "/");
					Maps.add(artifactStats, projectUrl, name, stats.get(url).count);
				}
			}
		}
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

	@Override
	public UsageStatData getUsageStatsForCode(String name, String url) {
		UsageStatData usageStatData = getUsageStats(name).get(url);
		return usageStatData == null ? empty : usageStatData;
	}

	@Override
	public UsageStatData getUsageStatsForArtifact(String name, String projectUrl) {
		Map<String, Integer> map = artifactStats.get(projectUrl);
		if (map != null) {
			Integer integer = map.get(name);
			if (integer != null)
				return new UsageStatData(integer);
		}
		return empty;
	}

	@Override
	public ISimpleMap<FriendData, UsageStatData> getFriendsCodeUsage(String url) {
		Map<FriendData, UsageStatData> result = new HashMap<FriendData, UsageStatData>();
		for (FriendData data : myFriends())
			result.put(data, getUsageStatsForCode(data.name, url));
		return SimpleMaps.<FriendData, UsageStatData> fromMap(result);
	}

	@Override
	public ISimpleMap<FriendData, UsageStatData> getFriendsArtifactUsage(String url) {
		Map<FriendData, UsageStatData> result = new HashMap<FriendData, UsageStatData>();
		for (FriendData data : myFriends())
			result.put(data, getUsageStatsForArtifact(data.name, url));
		return SimpleMaps.<FriendData, UsageStatData> fromMap(result);
	}
}

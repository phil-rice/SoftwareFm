package org.softwareFm.utilities.maps;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.utilities.strings.Strings;

public class UrlCache<V> {

	private final Map<String, V> map = Maps.newMap();
	private final Object lock = new Object();

	public boolean containsKey(String url) {
		return map.containsKey(url);
	}

	public V get(String url) {
		return map.get(url);
	}

	public V findOrCreate(String url, Callable<V> creator) {
		synchronized (lock) {
			return Maps.findOrCreate(map, url, creator);
		}
	}

	public void clear(String url) {
		synchronized (lock) {
			removeParents(url);
			map.remove(url);
		}
	}

	private void removeParents(String url) {
		String thisUrl = url;
		while (true) {
			String nextUrl = Strings.allButLastSegment(thisUrl, "/");
			if (nextUrl.equals(thisUrl))
				return;
			map.remove(nextUrl);
			thisUrl = nextUrl;
		}
	}
}

package org.softwareFm.utilities.maps;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.utilities.strings.Strings;
import org.softwareFm.utilities.url.Urls;

public class UrlCache<V> {

	private final Map<String, V> map = Maps.newMap();
	private final Object lock = new Object();

	public boolean containsKey(String url) {
		synchronized (lock) {
			return map.containsKey(Urls.removeSlash(url));
		}
	}

	public V get(String url) {
		synchronized (lock) {
			return map.get(Urls.removeSlash(url));
		}
	}

	public V findOrCreate(String url, Callable<V> creator) {
		synchronized (lock) {
			return Maps.findOrCreate(map, Urls.removeSlash(url), creator);
		}
	}

	public void clear(String url) {
		synchronized (lock) {
			String actualUrl = Urls.removeSlash(url);
			removeParents(actualUrl);
			map.remove(actualUrl);
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

	public void clear() {
		synchronized (lock) {
			map.clear();
		}

	}
}

/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.maps;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.url.Urls;

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

	public void put(String url, V value) {
		synchronized (lock) {
			map.put(Urls.removeSlash(url), value);
		}

	}

	public void clear() {
		synchronized (lock) {
			map.clear();
		}

	}
}
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.maps.UrlCache;
import org.softwareFm.common.maps.UrlCacheTest;
import org.softwareFm.common.url.Urls;
import org.softwareFm.server.processors.AbstractProcessCallTest;

public class AboveRepositoryCacheTest extends AbstractProcessCallTest<GitGetProcessor> {

	private UrlCache<String> cache;

	public void test() {
		String empty = "{\"data\":{}}";
		checkGetWhenNotIn("a/b/c/d", empty);
		checkGetWhenNotIn("a/b/c", empty);
		checkGetWhenNotIn("a/b/d", empty);
		checkGetWhenNotIn("a/b", empty);
		checkGetWhenNotIn("a", empty);

		checkGetWhenIn("a/b/c/d", empty);
		checkGetWhenIn("a/b/d", empty);
		checkGetWhenIn("a/b/c", empty);
		checkGetWhenIn("a/b", empty);
		checkGetWhenIn("a", empty);

		assertTrue(cache.containsKey("a"));
		assertTrue(cache.containsKey("a/b"));
		assertTrue(cache.containsKey("a/b/c"));
		assertTrue(cache.containsKey("a/b/c/d"));
		assertTrue(cache.containsKey("a/b/d"));

		makeRepo("a/b/c");

		assertFalse(cache.containsKey("a"));
		assertFalse(cache.containsKey("a/b"));
		assertFalse(cache.containsKey("a/b/c"));
		assertTrue(cache.containsKey("a/b/c/d"));
		assertTrue(cache.containsKey("a/b/d"));

		checkGetWhenNotIn("a", "{\"data\":{\"b\":{}}}");
		checkGetWhenNotIn("a/b", "{\"data\":{\"c\":{}}}");

		checkGetWhenIn("a", "{\"data\":{\"b\":{}}}");
		checkGetWhenIn("a/b", "{\"data\":{\"c\":{}}}");
	}

	private void makeRepo(String url) {
		RequestLine requestLine = makeRequestLine(CommonConstants.POST, Urls.composeWithSlash(CommonConstants.makeRootPrefix, url));
		Map<String, Object> empty = Maps.emptyStringObjectMap();
		MakeRootProcessor makeRootProcessor = new MakeRootProcessor(cache, remoteOperations);
		makeRootProcessor.process(requestLine, empty);
	}

	private void checkGetWhenNotIn(String url, String expected) {
		assertFalse(cache.containsKey(url));
		processor.process(makeRequestLine(CommonConstants.GET, url), Maps.emptyStringObjectMap());
		UrlCacheTest.checkInCache(cache, url, expected);
	}

	private void checkGetWhenIn(String url, String expected) {
		UrlCacheTest.checkInCache(cache, url, expected);
		processor.process(makeRequestLine(CommonConstants.GET, url), Maps.emptyStringObjectMap());
		UrlCacheTest.checkInCache(cache, url, expected);
	}

	@Override
	protected GitGetProcessor makeProcessor() {
		cache = new UrlCache<String>();
		return new GitGetProcessor(remoteOperations, cache);
	}

}
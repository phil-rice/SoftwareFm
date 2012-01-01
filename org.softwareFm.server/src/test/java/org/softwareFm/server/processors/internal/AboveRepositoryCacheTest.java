package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.maps.UrlCache;
import org.softwareFm.utilities.maps.UrlCacheTest;
import org.softwareFm.utilities.strings.Urls;

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
		RequestLine requestLine = makeRequestLine(ServerConstants.POST, Urls.composeWithSlash(ServerConstants.makeRootPrefix, url));
		Map<String, Object> empty = Maps.emptyStringObjectMap();
		MakeRootProcessor makeRootProcessor = new MakeRootProcessor(cache, remoteGitServer);
		makeRootProcessor.process(requestLine, empty);
	}

	private void checkGetWhenNotIn(String url, String expected) {
		assertFalse(cache.containsKey(url));
		processor.process(makeRequestLine(ServerConstants.GET, url), Maps.emptyStringObjectMap());
		UrlCacheTest.checkInCache(cache, url, expected);
	}

	private void checkGetWhenIn(String url, String expected) {
		UrlCacheTest.checkInCache(cache, url, expected);
		processor.process(makeRequestLine(ServerConstants.GET, url), Maps.emptyStringObjectMap());
		UrlCacheTest.checkInCache(cache, url, expected);
	}

	@Override
	protected GitGetProcessor makeProcessor() {
		cache = new UrlCache<String>();
		return new GitGetProcessor(remoteGitServer, cache);
	}

}

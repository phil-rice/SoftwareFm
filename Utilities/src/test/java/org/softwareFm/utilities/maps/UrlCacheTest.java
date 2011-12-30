package org.softwareFm.utilities.maps;

import junit.framework.TestCase;

import org.softwareFm.utilities.runnable.Callables;
import org.softwareFm.utilities.runnable.CountCallable;

public class UrlCacheTest extends TestCase {

	private final String a = "a/value";
	private final String ab = "a/b/value";
	private final String abc = "a/b/c/value";
	private final String abd = "a/b/d/value";

	private final String ap = "a/valuep";
	private final String abp = "a/b/valuep";
	private final String abcp = "a/b/c/valuep";
	private final String abdp = "a/b/d/valuep";

	public void testFindOrCreateDoesntAffectOthers() {
		UrlCache<Object> cache = new UrlCache<Object>();
		checkCreates(cache, "a", a);
		checkCreates(cache, "a/b", ab);
		checkCreates(cache, "a/b/c", abc);

		checkInCache(cache, "a", a);
		checkInCache(cache, "a/b", ab);
		checkInCache(cache, "a/b/c", abc);
	}

	public void testDepth0FindOrCreateReversed() {
		UrlCache<Object> cache = new UrlCache<Object>();
		checkCreates(cache, "a/b/c", abc);
		checkCreates(cache, "a/b", ab);
		checkCreates(cache, "a", a);

		checkInCache(cache, "a", a);
		checkInCache(cache, "a/b", ab);
		checkInCache(cache, "a/b/c", abc);
	}

	public void testClearNukesEverythingAbove() {
		UrlCache<Object> cache = new UrlCache<Object>();
		checkCreates(cache, "a/b/c", abc);
		checkCreates(cache, "a/b", ab);
		checkCreates(cache, "a", a);

		cache.clear("a");
		checkCreates(cache, "a", ap);
		checkInCache(cache, "a/b", ab);
		checkInCache(cache, "a/b/c", abc);

		cache.clear("a/b");
		checkCreates(cache, "a", ap);
		checkCreates(cache, "a/b", abp);
		checkInCache(cache, "a/b/c", abc);

		cache.clear("a/b/c");
		checkCreates(cache, "a", ap);
		checkCreates(cache, "a/b", abp);
		checkCreates(cache, "a/b/c", abcp);
	}

	public void testDepthClearDoesntClearUrlsUnderIt() {
		UrlCache<Object> cache = new UrlCache<Object>();
		checkCreates(cache, "a/b/c", abc);
		checkCreates(cache, "a/b", ab);
		checkCreates(cache, "a", a);

		cache.clear("a/b");
		checkCreates(cache, "a/b", ab);
		checkInCache(cache, "a/b/c", abc);
	}


	public static void checkCreates(UrlCache<Object> cache, String key, final String value) {
		CountCallable<Object> count = Callables.<Object> count(value);
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(1, count.getCount());
	}

	public static <T>void checkInCache(UrlCache<T> cache, String key, final T value) {
		CountCallable<T> count = Callables.<T> count(value);
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(0, count.getCount());
	}

}

/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.maps;

import junit.framework.TestCase;

import org.softwareFm.common.maps.UrlCache;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.runnable.CountCallable;

public class UrlCacheTest extends TestCase {

	private final String a = "a/value";
	private final String ab = "a/b/value";
	private final String abc = "a/b/c/value";

	private final String ap = "a/valuep";
	private final String abp = "a/b/valuep";
	private final String abcp = "a/b/c/valuep";

	public void testFindOrCreateDoesntAffectOthers() {
		UrlCache<Object> cache = new UrlCache<Object>();
		checkCreates(cache, "a", a);
		checkCreates(cache, "a/b", ab);
		checkCreates(cache, "a/b/c", abc);

		checkInCache(cache, "a", a);
		checkInCache(cache, "a/b", ab);
		checkInCache(cache, "a/b/c", abc);
	}

	public void testDoesntCareIfHasSlash1() {
		UrlCache<Object> cache = new UrlCache<Object>();
		checkCreates(cache, "a", a);
		checkCreates(cache, "a/b", ab);
		checkCreates(cache, "a/b/c", abc);

		checkInCache(cache, "/a", a);
		checkInCache(cache, "/a/b", ab);
		checkInCache(cache, "/a/b/c", abc);

	}

	public void testDoesntCareIfHasSlash2() {
		UrlCache<Object> cache = new UrlCache<Object>();
		checkCreates(cache, "/a", a);
		checkCreates(cache, "/a/b", ab);
		checkCreates(cache, "/a/b/c", abc);

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

	public void testClearNukesTheCache() {
		UrlCache<Object> cache = new UrlCache<Object>();
		checkCreates(cache, "a/b/c", abc);
		checkCreates(cache, "a/b", ab);
		checkCreates(cache, "a", a);
		cache.clear();
		checkCreates(cache, "a/b/c", abc);
		checkCreates(cache, "a/b", ab);
		checkCreates(cache, "a", a);
	}

	public static void checkCreates(UrlCache<Object> cache, String key, final String value) {
		assertFalse(cache.containsKey(key));
		CountCallable<Object> count = Callables.<Object> count(value);
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(1, count.getCount());
		assertEquals(value, cache.get(key));
		assertTrue(cache.containsKey(key));
	}

	public static <T> void checkInCache(UrlCache<T> cache, String key, final T value) {
		assertTrue(cache.containsKey(key));
		CountCallable<T> count = Callables.<T> count(value);
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(value, cache.findOrCreate(key, count));
		assertEquals(0, count.getCount());
		assertTrue(cache.containsKey(key));
		assertEquals(value, cache.get(key));
	}

}
package org.arc4eclipse.repositoryFacard.impl;

import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;

import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.utilities.maps.Maps;

public class RepositoryFacardTest extends TestCase {

	public void testFacardDelegatesGet() {
		RepositoryClientMock<String, String> mock = new RepositoryClientMock<String, String>();
		IRepositoryFacard<String, String, String, Map<Object, Object>> facard = IRepositoryFacard.Utils.facard(mock);
		checkGetDetails(facard, "key", "thing", "aspect", 1, 0);
	}

	public void testFacardCachesGetBasedOnKeyAndAspect() {
		RepositoryClientMock<String, String> mock = new RepositoryClientMock<String, String>();
		IRepositoryFacard<String, String, String, Map<Object, Object>> facard = IRepositoryFacard.Utils.facard(mock);

		for (int i = 0; i < 2; i++) {
			checkGetDetails(facard, "key", "thing", "aspect1", 1, 0);
			checkGetDetails(facard, "key1", "thing", "aspect1", 2, 0);
			checkGetDetails(facard, "key", "thing", "aspect2", 3, 0);
			checkGetDetails(facard, "key1", "thing", "aspect2", 4, 0);
		}
	}

	public void testFacardReturnsEmptyWhenStatusCodeIsNot200() {
		RepositoryClientMock<String, String> mock = new RepositoryClientMock<String, String>(199);
		IRepositoryFacard<String, String, String, Map<Object, Object>> facard = IRepositoryFacard.Utils.facard(mock);
		checkReturnsEmpty(facard);
	}

	private void checkReturnsEmpty(IRepositoryFacard<String, String, String, Map<Object, Object>> facard) {
		MemoryRepositoryFacardGetCallback<String, String, String, Map<Object, Object>> callback = new MemoryRepositoryFacardGetCallback<String, String, String, Map<Object, Object>>();
		facard.getDetails("key", "thing", "aspect", callback);
		assertEquals(1, callback.count.get());
		assertEquals("key", callback.key);
		assertEquals("thing", callback.thing);
		assertEquals("aspect", callback.aspect);
		assertEquals(Collections.emptyMap(), callback.data);
	}

	private void checkGetDetails(IRepositoryFacard<String, String, String, Map<Object, Object>> facard, String key, String thing, String aspect, long getCount, long putCount) {
		MemoryRepositoryFacardGetCallback<String, String, String, Map<Object, Object>> callback = new MemoryRepositoryFacardGetCallback<String, String, String, Map<Object, Object>>();
		facard.getDetails(key, thing, aspect, callback);
		assertEquals(1, callback.count.get());
		assertEquals(key, callback.key);
		assertEquals(thing, callback.thing);
		assertEquals(aspect, callback.aspect);
		assertEquals(Maps.makeMap("put", putCount, "get", getCount), callback.data);
	}

}

package org.softwareFm.card.internal;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.softwareFm.utilities.maps.Maps;

public class CardDataStoreCacheTest extends TestCase {

	private CardDataStoreMock raw;
	private CardDataStoreCache cache;

	@SuppressWarnings("unchecked")
	public void testCallsRaw() {
		CacheDataStoreCallbackMock mock = new CacheDataStoreCallbackMock();
		
		cache.processDataFor(CardDataStoreFixture.url1a, mock);
		cache.processDataFor(CardDataStoreFixture.url1b, mock);
		assertEquals(Arrays.asList(CardDataStoreFixture.url1a, CardDataStoreFixture.url1b), mock.urls);
		assertEquals(Arrays.asList(CardDataStoreFixture.data1a, CardDataStoreFixture.data1b), mock.results);
		assertEquals(Maps.makeMap(CardDataStoreFixture.url1a, 1, CardDataStoreFixture.url1b, 1), raw.counts);
	}

	@SuppressWarnings("unchecked")
	public void testCallsCallbackButDoesntCallRawIfHasCachedValue() {
		CacheDataStoreCallbackMock mock = new CacheDataStoreCallbackMock();
		cache.processDataFor(CardDataStoreFixture.url1a, mock);
		cache.processDataFor(CardDataStoreFixture.url1b, mock);
		cache.processDataFor(CardDataStoreFixture.url1a, mock);
		cache.processDataFor(CardDataStoreFixture.url1b, mock);
		cache.processDataFor(CardDataStoreFixture.url1b, mock);
		cache.processDataFor(CardDataStoreFixture.url1a, mock);
		assertEquals(Arrays.asList(CardDataStoreFixture.url1a, CardDataStoreFixture.url1b, CardDataStoreFixture.url1a, CardDataStoreFixture.url1b, CardDataStoreFixture.url1b, CardDataStoreFixture.url1a), mock.urls);
		assertEquals(Arrays.asList(//
				CardDataStoreFixture.data1a, CardDataStoreFixture.data1b,//
				CardDataStoreFixture.data1a, CardDataStoreFixture.data1b,//
				CardDataStoreFixture.data1b, CardDataStoreFixture.data1a), mock.results);
		assertEquals(Maps.makeMap(CardDataStoreFixture.url1a, 1, CardDataStoreFixture.url1b, 1), raw.counts);
	}

	public void testDoesntCallNotInWhenDataPresent() {
		CacheDataStoreCallbackMock mock = new CacheDataStoreCallbackMock();
		cache.processDataFor(CardDataStoreFixture.url1a, mock);
		cache.processDataFor(CardDataStoreFixture.url1b, mock);
		cache.processDataFor(CardDataStoreFixture.url1a, mock);
		cache.processDataFor(CardDataStoreFixture.url1b, mock);
		cache.processDataFor(CardDataStoreFixture.url1b, mock);
		cache.processDataFor(CardDataStoreFixture.url1a, mock);
		assertEquals(Collections.emptyList(), mock.noDataUrls);
	}

	public void testCallsNoDataIfNoData() {
		CacheDataStoreCallbackMock mock = new CacheDataStoreCallbackMock();
		cache.processDataFor("notIn", mock);
		assertEquals(Arrays.asList("notIn"), mock.noDataUrls);
		assertEquals(Maps.makeMap("notIn", 1), raw.counts);
	}

	public void testCallsCallbackButNotRawIfNoData() {
		CacheDataStoreCallbackMock mock = new CacheDataStoreCallbackMock();
		cache.processDataFor("notIn1", mock);
		cache.processDataFor("notIn1", mock);
		cache.processDataFor("notIn2", mock);
		cache.processDataFor("notIn1", mock);
		assertEquals(Arrays.asList("notIn1", "notIn1", "notIn2", "notIn1"), mock.noDataUrls);
		assertEquals(Maps.makeMap("notIn1", 1, "notIn2", 1), raw.counts);
	}

	public void testDoesntCallProcessIfNoData() {
		CacheDataStoreCallbackMock mock = new CacheDataStoreCallbackMock();
		cache.processDataFor("notIn1", mock);
		cache.processDataFor("notIn1", mock);
		cache.processDataFor("notIn2", mock);
		cache.processDataFor("notIn1", mock);
		assertEquals(Collections.emptyList(), mock.urls);
	}

	@SuppressWarnings("unchecked")
	public void testReturnsNewValueWhenPutWhenInitiallyIn() {
		CacheDataStoreCallbackMock mock = new CacheDataStoreCallbackMock();
		cache.processDataFor(CardDataStoreFixture.url1a, mock);
		cache.put(CardDataStoreFixture.url1a, CardDataStoreFixture.p1q2);
		cache.processDataFor(CardDataStoreFixture.url1a, mock);
		assertEquals(Arrays.asList(CardDataStoreFixture.url1a, CardDataStoreFixture.url1a), mock.urls);
		assertEquals(Arrays.asList(CardDataStoreFixture.data1a, CardDataStoreFixture.data1aWithP1Q2), mock.results);
		assertEquals(Maps.makeMap(CardDataStoreFixture.url1a, 2), raw.counts);
	}

	@SuppressWarnings("unchecked")
	public void testReturnsNewValueWhenNotInitiallyIn() {
		CacheDataStoreCallbackMock mock = new CacheDataStoreCallbackMock();
		cache.processDataFor("url", mock);
		cache.put("url", CardDataStoreFixture.p1q2);
		cache.processDataFor("url", mock);
		assertEquals(Arrays.asList("url"), mock.noDataUrls);
		assertEquals(Arrays.asList("url"), mock.urls);
		assertEquals(Arrays.asList(CardDataStoreFixture.mapP1Q2), mock.results);
		assertEquals(Maps.makeMap("url", 2), raw.counts);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		raw = CardDataStoreFixture.rawCardStore();
		cache = new CardDataStoreCache(raw);
	}
}

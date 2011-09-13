package org.softwareFm.utilities.maps;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import junit.framework.TestCase;

import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.tests.Tests;

public abstract class AbstractSimpleMapTest<K, V> extends TestCase {

	private K key1;
	private K key2;
	private V value1;
	private V value2;

	abstract protected K makeKey(String seed);

	abstract protected V makeValue(String seed);

	abstract protected ISimpleMap<K, V> blankMap();

	abstract protected String duplicateKeyErrorMessage();

	abstract protected void put(ISimpleMap<K, V> map, K key, V value);

	public void testBlankMap() {
		ISimpleMap<K, V> map = blankMap();
		assertEquals(Collections.emptyList(), map.keys());
	}

	public void testThrowsExceptionIfPutSameKey() {
		final ISimpleMap<K, V> map = blankMap();
		put(map, key1, value1);
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				put(map, key1, value2);
			}
		});
		assertEquals(duplicateKeyErrorMessage(), e.getMessage());
		assertSame(value1, map.get(key1));
	}

	public void testCanStoreAndRetrieveActions() {
		ISimpleMap<K, V> map = blankMap();
		put(map, key1, value1);
		put(map, key2, value2);

		assertSame(value1, map.get(key1));
		assertSame(value2, map.get(key2));
	}

	@SuppressWarnings("unchecked")
	public void testKeys() {
		ISimpleMap<K, V> map = blankMap();
		put(map, key1, value1);
		assertEquals(Arrays.asList("a"), map.keys());
		put(map, key2, value2);

		assertEquals(Sets.<K> makeSet(key1, key2), new HashSet<K>(map.keys()));
	}

	protected IllegalArgumentException checkThrowsExceptionIfKeyNotThere() {
		final ISimpleMap<K, V> map = blankMap();
		put(map, key1, value1);
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				map.get(key2);
			}
		});
		return e;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		key1 = makeKey("a");
		key2 = makeKey("b");
		value1 = makeValue("1");
		value2 = makeValue("2");
	}
}

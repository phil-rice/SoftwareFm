/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.maps;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import junit.framework.TestCase;

import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.maps.ISimpleMap;
import org.softwareFm.common.tests.Tests;

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
		assertEquals(Arrays.asList(key1), map.keys());
		put(map, key2, value2);

		assertEquals(Sets.<K> makeSet(key1, key2), new HashSet<K>(map.keys()));
	}

	protected IllegalArgumentException checkThrowsExceptionIfKeyNotThere(String message) {
		final ISimpleMap<K, V> map = blankMap();
		put(map, key1, value1);
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				map.get(key2);
			}
		});
		assertEquals(message, e.getMessage());
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
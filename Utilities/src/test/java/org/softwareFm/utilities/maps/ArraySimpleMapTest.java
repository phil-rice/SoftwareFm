package org.softwareFm.utilities.maps;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class ArraySimpleMapTest extends TestCase {

	private final String key1 = "k1";
	private final String key1a = new String("k1");
	private final String key2 = "k2";
	private final String key3 = "k3";
	private final List<String> keys = Arrays.asList(key1, key2, key3);

	public void testGet() {
		ArraySimpleMap<String, String> arraySimpleMap = makeMap();
		assertEquals("v1", arraySimpleMap.get(key1));
		assertEquals("v2", arraySimpleMap.get(key2));
		assertEquals("v3", arraySimpleMap.get(key3));
		assertEquals(null, arraySimpleMap.get("not a key"));
		assertEquals("v1", arraySimpleMap.get(key1a));
	}

	public void testSetValuesFrom() {
		ArraySimpleMap<String, String> arraySimpleMap = makeMap();
		arraySimpleMap.setValuesFrom(Arrays.asList("v1a", "v2a", "v3a"));
		assertEquals("v1a", arraySimpleMap.get(key1));
		assertEquals("v2a", arraySimpleMap.get(key2));
		assertEquals("v3a", arraySimpleMap.get(key3));
	}

	private ArraySimpleMap<String, String> makeMap() {
		ArraySimpleMap<String, String> arraySimpleMap = new ArraySimpleMap<String, String>(keys, String.class);
		String[] values = arraySimpleMap.getValues();
		assertEquals(3, values.length);
		values[0] = "v1";
		values[1] = "v2";
		values[2] = "v3";
		return arraySimpleMap;
	}

}

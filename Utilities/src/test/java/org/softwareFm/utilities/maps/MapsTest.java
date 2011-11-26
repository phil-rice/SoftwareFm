/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.maps;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.utilities.constants.UtilityMessages;
import org.softwareFm.utilities.functions.Functions;

@SuppressWarnings("unchecked")
public class MapsTest extends TestCase {

	private static final Map<String, Object> input = Maps.stringObjectLinkedMap(//
			"key1", Maps.stringObjectLinkedMap("a", 1, "b", 2),//
			"key2", Maps.stringObjectLinkedMap("a", 2, "b", 1));

	private static final MapAsList expectedabk12 = new MapAsList(Arrays.asList("a", "b", "key"), 2, Arrays.<Object> asList(1, 2, "key1"), Arrays.<Object> asList(2, 1, "key2"));
	private static final MapAsList expectedabk21 = new MapAsList(Arrays.asList("a", "b", "key"), 2, Arrays.<Object> asList(2, 1, "key2"), Arrays.<Object> asList(1, 2, "key1"));

	private static final MapAsList expectedbak12 = new MapAsList(Arrays.asList("b", "a", "key"), 2, Arrays.<Object> asList(2, 1, "key1"), Arrays.<Object> asList(1, 2, "key2"));
	private static final MapAsList expectedbak21 = new MapAsList(Arrays.asList("b", "a", "key"), 2, Arrays.<Object> asList(1, 2, "key2"), Arrays.<Object> asList(2, 1, "key1"));

	private static final MapAsList expectedkab12 = new MapAsList(Arrays.asList("key", "a", "b"), 0, Arrays.<Object> asList("key1", 1, 2), Arrays.<Object> asList("key2", 2, 1));
	private static final MapAsList expectedkab21 = new MapAsList(Arrays.asList("key", "a", "b"), 0, Arrays.<Object> asList("key2", 2, 1), Arrays.<Object> asList("key1", 1, 2));

	public void testMapsAsList() {
		assertEquals(expectedabk12, Maps.toMapAsList("key", input, "a", Arrays.<String> asList()));
		assertEquals(expectedabk12, Maps.toMapAsList("key", input, "a", Arrays.<String> asList("a")));
		assertEquals(expectedabk12, Maps.toMapAsList("key", input, "a", Arrays.<String> asList("a", "b")));
		assertEquals(expectedabk12, Maps.toMapAsList("key", input, "a", Arrays.<String> asList("a", "b", "key")));

		assertEquals(expectedabk21, Maps.toMapAsList("key", input, "b", Arrays.<String> asList()));
		assertEquals(expectedabk21, Maps.toMapAsList("key", input, "b", Arrays.<String> asList("a")));
		assertEquals(expectedabk21, Maps.toMapAsList("key", input, "b", Arrays.<String> asList("a", "b")));
		assertEquals(expectedabk21, Maps.toMapAsList("key", input, "b", Arrays.<String> asList("a", "b", "key")));

		assertEquals(expectedbak12, Maps.toMapAsList("key", input, "a", Arrays.<String> asList("b")));
		assertEquals(expectedbak12, Maps.toMapAsList("key", input, "a", Arrays.<String> asList("b", "a")));
		assertEquals(expectedbak12, Maps.toMapAsList("key", input, "a", Arrays.<String> asList("b", "a", "key")));

		assertEquals(expectedbak21, Maps.toMapAsList("key", input, "b", Arrays.<String> asList("b")));
		assertEquals(expectedbak21, Maps.toMapAsList("key", input, "b", Arrays.<String> asList("b", "a")));
		assertEquals(expectedbak21, Maps.toMapAsList("key", input, "b", Arrays.<String> asList("b", "a", "key")));

		assertEquals(expectedkab12, Maps.toMapAsList("key", input, "a", Arrays.<String> asList("key")));
		assertEquals(expectedkab21, Maps.toMapAsList("key", input, "b", Arrays.<String> asList("key")));
	}

	public void testMakeMap() {
		checkMakeMap();
		checkMakeMap("a", 1);
		checkMakeMap("a", 1, "b", 2);

		try {
			Maps.makeMap("a", 1, "a", 2);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals(MessageFormat.format(UtilityMessages.duplicateKey, "a", "1", "2"), e.getMessage());
		}
	}

	public void testNewMap() {
		Map<Object, Object> map = Maps.newMap();
		assertEquals(0, map.size());
		map.put("a", 1);
		assertEquals(1, map.get("a"));
	}

	private void checkMakeMap(Object... expected) {
		Map<Object, Object> actual = Maps.makeMap(expected);
		assertEquals(expected.length / 2, actual.size());
		for (int i = 0; i < expected.length; i += 2) {
			Object key = expected[i + 0];
			Object value = expected[i + 1];
			assertEquals(value, actual.get(key));
		}
	}

	public void testAddToMap() {
		Map<Object, List<Object>> map = Maps.newMap();
		Maps.addToList(map, "a", 1);
		Maps.addToList(map, "a", 2);
		Maps.addToList(map, "a", 3);
		Maps.addToList(map, "b", 4);
		Maps.addToList(map, "b", 5);
		assertEquals(Maps.makeMap("a", Arrays.asList(1, 2, 3), "b", Arrays.asList(4, 5)), map);
	}

	public void testPartitionMapByValueClass() {
		checkPartitionMapByValueClass(Maps.makeMap("a", 1, "b", 2, "c", "3", "d", "4"), //
				Maps.makeMap(Integer.class, Maps.makeMap("a", 1, "b", 2), String.class, Maps.makeMap("c", "3", "d", "4")),//
				Integer.class, String.class);
		checkPartitionMapByValueClass(Maps.makeMap("a", 1, "b", 2, "c", "3", "d", "4"), //
				Maps.makeMap(Integer.class, Maps.makeMap("a", 1, "b", 2), Object.class, Maps.makeMap("c", "3", "d", "4")),//
				Integer.class, Object.class);
	}

	public void testNewMapWithClassParameter() {
		checkNewMap(HashMap.class);
		checkNewMap(IdentityHashMap.class);
		checkNewMap(LinkedHashMap.class);
	}

	private void checkNewMap(Class<?> clazz) {
		Map<Object, Object> map = Maps.newMap(clazz);
		assertTrue(clazz.isAssignableFrom(map.getClass()));
		assertEquals(0, map.size());
	}

	public void testNewMapOfSameType() {
		checkNewMapOfSameType(HashMap.class);
		checkNewMapOfSameType(IdentityHashMap.class);
		checkNewMapOfSameType(LinkedHashMap.class);
	}

	private void checkNewMapOfSameType(Class<?> clazz) {
		Map<Object, Object> oldMap = Maps.newMap(clazz);
		Map<Object, Object> map = Maps.newMapOfSameTypeMap(oldMap);
		assertTrue(clazz.isAssignableFrom(map.getClass()));
		assertEquals(0, map.size());
	}

	public void testCopy() {
		checkCopy(HashMap.class);
		checkCopy(IdentityHashMap.class);
		checkCopy(LinkedHashMap.class);
	}

	private void checkCopy(Class<?> clazz) {
		Map<Object, Object> oldMap = Maps.newMap(clazz);
		oldMap.put("a", 1);
		oldMap.put("b", 2);
		Map<Object, Object> map = Maps.copyMap(oldMap);
		assertTrue(clazz.isAssignableFrom(map.getClass()));
		assertEquals(oldMap, map);
		assertNotSame(oldMap, map);
	}

	@SuppressWarnings("rawtypes")
	public void testGetWithKeys() {
		Map<Object, Object> map = Maps.makeMap("a", 1, "b", 2, "c", Maps.makeMap("d", 4, "e", 5, "f", Maps.makeMap("g", 7)));
		assertEquals(1, Maps.get(map, "a"));
		assertEquals(2, Maps.get(map, "b"));
		assertEquals(map.get("c"), Maps.get(map, "c"));
		assertEquals(4, Maps.get(map, "c", "d"));
		assertEquals(5, Maps.get(map, "c", "e"));
		assertEquals(((Map) map.get("c")).get("f"), Maps.get(map, "c", "f"));
		assertEquals(7, Maps.get(map, "c", "f", "g"));
	}

	public void testPutWithKeys() {
		Map<Object, Object> map = Maps.makeMap("a", 1, "b", 2, "c", Maps.makeMap("d", 4, "e", 5, "f", Maps.makeMap("g", 7)));
		Maps.put(map, new String[] { "a" }, 11);
		Maps.put(map, new String[] { "b" }, 12);
		Maps.put(map, new String[] { "c", "d" }, 14);
		Maps.put(map, new String[] { "c", "e" }, 15);
		Maps.put(map, new String[] { "c", "f", "g" }, 17);
		assertEquals(Maps.makeMap("a", 11, "b", 12, "c", Maps.makeMap("d", 14, "e", 15, "f", Maps.makeMap("g", 17))), map);
	}

	public void testCopyMapWith() {
		checkCopyMapWith(HashMap.class);
		checkCopyMapWith(IdentityHashMap.class);
		checkCopyMapWith(LinkedHashMap.class);
	}

	private void checkCopyMapWith(Class<?> clazz) {
		Map<Object, Object> oldMap = Maps.newMap(clazz);
		oldMap.put("a", 1);
		oldMap.put("b", 2);
		Map<Object, Object> copyOfOld = Maps.copyMap(oldMap);
		Map<Object, Object> expected = Maps.copyMap(oldMap);
		expected.put("c", 3);

		Map<Object, Object> map = Maps.with(oldMap, "c", 3);
		assertTrue(clazz.isAssignableFrom(map.getClass()));
		assertEquals(copyOfOld, oldMap);
		assertEquals(expected, map);
	}

	@SuppressWarnings("rawtypes")
	private void checkPartitionMapByValueClass(Map<Object, Object> input, Map<Object, Object> expected, Class... partitionClasses) {
		Map<Class, Map<Object, Object>> actual = Maps.partitionByValueClass(input, LinkedHashMap.class, partitionClasses);
		assertEquals(expected, actual);

	}

	public void testToParameters() {
		check(Object.class);
		check(String.class);
		check(Object.class, "a", "1", "b", "2");
		check(String.class, "a", "1", "b", "2");
	}

	private <T> void check(Class<T> toClass, Object... params) {
		Map<Object, Object> firstMap = Maps.makeMap(params);
		T[] foundParameters = Maps.toParameters(firstMap, Functions.<Object, T> identity(), Functions.<Object, T> identity(), toClass);
		Map<Object, Object> secondMap = Maps.makeMap(foundParameters);
		assertEquals(firstMap, secondMap);

	}
}
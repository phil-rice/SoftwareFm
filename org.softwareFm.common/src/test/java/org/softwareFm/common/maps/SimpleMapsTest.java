/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.ISimpleMap;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.maps.SimpleMaps;

public class SimpleMapsTest extends TestCase {
	private final ISimpleMap<Object, Object> smA1B2 = SimpleMaps.makeMap("a", 1, "b", 2);
	private final ISimpleMap<Object, Object> smB2C3 = SimpleMaps.makeMap("b", 2, "c", 3);
	private final ISimpleMap<Object, Object> smA1C3 = SimpleMaps.makeMap("a", 1, "c", 3);
	private final ISimpleMap<Object, Object> smA1B2C3 = SimpleMaps.makeMap("a", 1, "b", 2, "c", 3);

	private final Map<Object, Object> ma1b2 = Maps.makeLinkedMap("a", 1, "b", 2);
	private final Map<Object, Object> ma1b2c3 = Maps.makeLinkedMap("a", 1, "b", 2, "c", 3);

	public void testMakeMap() {
		checkMatchesLinkedMap(smA1B2, ma1b2);
	}

	@SuppressWarnings("unchecked")
	public void testToListOfMaps() {
		List<Map<Object, Object>> maps = SimpleMaps.toListOfMaps(Arrays.asList(smA1B2, smA1B2C3));
		assertEquals(Arrays.asList(ma1b2, ma1b2c3), maps);
	}

	public void testFromMap() {
		checkMatchesLinkedMap(SimpleMaps.fromMap(ma1b2), ma1b2);
	}

	@SuppressWarnings("unchecked")
	public void testMerge() {
		assertEquals(ma1b2c3, SimpleMaps.merge(smA1B2, smA1B2C3));
		assertEquals(ma1b2c3, SimpleMaps.merge(Arrays.asList(smA1B2, smA1C3)));
	}

	public void testEmpty() {
		checkMatchesLinkedMap(SimpleMaps.empty(), Collections.emptyMap());
	}

	public void testAggregateKeysOfChildMaps() {
		List<Object> result = SimpleMaps.aggregateKeysOfChildMaps(Maps.<String, ISimpleMap<Object, Object>> makeLinkedMap("p", smA1B2, "1", smB2C3));
		assertEquals(Arrays.asList("a", "b", "c"), result);
	}

	public void testPartitionByValue() {
		Map<Object, Map<Object, Object>> result = SimpleMaps.partitionByValue(smA1B2C3, new IFunction1<Object, Object>() {
			@Override
			public Object apply(Object from) throws Exception {
				if (from.equals(1))
					return "p";
				if (from.equals(3))
					return "p";
				if (from.equals(2))
					return "q";
				throw new IllegalArgumentException(from.toString());
			}
		});
		assertEquals(Maps.makeMap("p", Maps.makeMap("a", 1, "c", 3), "q", Maps.makeMap("b", 2)), result);
	}

	private <K, V> void checkMatchesLinkedMap(ISimpleMap<K, V> simpleMap, Map<K, V> map) {
		assertEquals(new HashSet<K>(simpleMap.keys()), new HashSet<K>(map.keySet()));
		assertEquals(simpleMap.keys().size(), map.size());
		assertEquals(simpleMap.keys(), new ArrayList<K>(map.keySet()));
		for (K key : simpleMap.keys())
			assertEquals(map.get(key), simpleMap.get(key));
	}

}
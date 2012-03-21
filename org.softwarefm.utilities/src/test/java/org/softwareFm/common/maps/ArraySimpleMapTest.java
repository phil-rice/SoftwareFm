/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.maps;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.maps.ArraySimpleMap;

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
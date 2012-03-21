/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class JsonTest extends TestCase {

	public void testParse() {
		assertEquals("1", Json.parse("\"1\""));
		assertEquals(Arrays.asList(1l, 2l, 3l), Json.parse("[1,2,3]"));
		assertEquals(1l, Json.parse("1"));
		assertEquals(Maps.makeMap("a", 1l, "b", 2l), Json.parse("{\"a\": 1, \"b\":2}"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void _testSingleQuotes() throws ParseException {
		assertEquals(Maps.makeMap("a", 1l, "b", 2l), Json.parse("{'a': 1, 'b':2}"));
		Object actual = JSONValue.parseWithException("{'a': 1, 'b':2}");
		Map<String, Long> expected = new HashMap<String, Long>();
		expected.put("a", 1L);
		expected.put("b", 2L);
		Maps.assertEquals(expected, (Map) actual);
		fail("this is expected to fail, as the current Json implementation doesn't use single quotes");

	}

	public void test() {
		assertEquals(Maps.makeMap("a", 1l, "b", 2l), Json.mapFromString("{\"a\":1, \"b\":2}"));
	}

}
package org.arc4eclipse.utilities.json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.arc4eclipse.utilities.maps.Maps;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class JsonTest extends TestCase {

	public void testParse() {
		assertEquals("1", Json.parse("\"1\""));
		assertEquals(Arrays.asList(1l, 2l, 3l), Json.parse("[1,2,3]"));
		assertEquals(1l, Json.parse("1"));
		assertEquals(Maps.makeMap("a", 1l, "b", 2l), Json.parse("{\"a\": 1, \"b\":2}"));
		assertEquals(Maps.makeMap("a", 1l, "b", 2l), Json.parse("{'a': 1, 'b':2}"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void _testSingleQuotes() throws ParseException {
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

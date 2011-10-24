package org.softwareFm.utilities.strings;

import junit.framework.TestCase;

public class StringsTest extends TestCase {

	public void testCamelCaseToPretty() {
		assertEquals("G", Strings.camelCaseToPretty("G"));
		assertEquals("", Strings.camelCaseToPretty(""));
		assertEquals("G", Strings.camelCaseToPretty("g"));
		assertEquals("G", Strings.camelCaseToPretty(" g"));
		assertEquals("G", Strings.camelCaseToPretty(" G"));

		assertEquals("Group", Strings.camelCaseToPretty("group"));
		assertEquals("Group Id", Strings.camelCaseToPretty("groupId"));
		assertEquals("Group Id Again", Strings.camelCaseToPretty("groupIdAgain"));
		assertEquals("Group Id Again", Strings.camelCaseToPretty("GroupIdAgain"));
		assertEquals("Group Id Again And Again", Strings.camelCaseToPretty("GroupIdAgain And Again"));
		assertEquals("Group Id Again And Again", Strings.camelCaseToPretty("GroupIdAgain     And   Again"));

	}

	public void testSplit() {
		assertEquals(new PreAndPost("a", "b"), Strings.split("a.b", '.'));
		assertEquals(new PreAndPost("a", "b"), Strings.split("a$b", '$'));
		assertEquals(new PreAndPost("ab", null), Strings.split("ab", '$'));
		assertEquals(new PreAndPost("ab", ""), Strings.split("ab$", '$'));
		assertEquals(new PreAndPost("", null), Strings.split("", '$'));
		assertEquals(new PreAndPost("", ""), Strings.split(".", '.'));
	}

	public void testSqlEscape() {
		assertEquals("abc", Strings.sqlEscape("abc"));
		assertEquals("a''bc", Strings.sqlEscape("a'bc"));
		assertEquals("a''''bc", Strings.sqlEscape("a''bc"));
		assertEquals("a''bc", Strings.sqlEscape("a\'bc"));
		assertEquals("a\\''bc", Strings.sqlEscape("a\\'bc"));
	}

	public void testOneLineLowWhiteSpace() {
		checkOneLineLowWhiteSpace("a b c", "   \n   a  \n\nb\n  c");
	}

	private void checkOneLineLowWhiteSpace(String expected, String input) {
		assertEquals(expected, Strings.oneLineLowWhiteSpace(input));

	}

	public void testKeepOnly() {
		assertEquals("1234", Strings.onlyKeep("raw12and3,4", "4231"));
	}

	public void testLastSegmentFn() throws Exception {
		assertEquals("whole", Strings.lastSegmentFn("/").apply("whole"));
		assertEquals("last", Strings.lastSegmentFn("/").apply("first/second/last"));
	}

}

/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.strings;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;

import org.softwarefm.utilities.functions.Functions;
import org.softwarefm.utilities.tests.Tests;

public class StringsTest extends TestCase {

	public void testFromHex() {
		checkFromHex("00", 0);
		checkFromHex("09", 9);
		checkFromHex("0a", 10);
		byte[] actual = Strings.fromHex("000102030405060708090a0b0c0d0e0f101112");
		assertTrue(Arrays.equals(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 }, actual));
	}

	public void testFirstNCharacters() {
		assertEquals(null, Strings.firstNCharacters(null, 5));
		assertEquals("abc", Strings.firstNCharacters("abcde", 3));
		assertEquals("ab", Strings.firstNCharacters("ab", 3));
		assertEquals("", Strings.firstNCharacters("", 3));
		Tests.assertThrowsWithMessage("Cannot get first (-1) characters", IllegalArgumentException.class, new Runnable() {
			
			public void run() {
				Strings.firstNCharacters("asdjk", -1);
			}
		});
	}

	private void checkFromHex(String string, int... bytes) {
		byte[] expected = new byte[bytes.length];
		for (int i = 0; i < expected.length; i++)
			expected[i] = (byte) bytes[i];
		byte[] actual = Strings.fromHex(string);
		assertTrue(Arrays.equals(expected, actual));
	}

	public void testIsEmail() {
		assertEquals(true, Strings.isEmail("a@b.com"));
		assertEquals(true, Strings.isEmail("a.b@c.com"));

		assertEquals(false, Strings.isEmail("a@b"));
		assertEquals(false, Strings.isEmail("a@b@com"));
		assertEquals(false, Strings.isEmail("a.b@com "));
		assertEquals(false, Strings.isEmail(" a.b@com "));
		assertEquals(false, Strings.isEmail(" a.b @com "));
		assertEquals(false, Strings.isEmail(" ^.b @com "));
		assertEquals(false, Strings.isEmail("a_b @com "));
	}

	public void testHtmlEscape() {
		assertEquals("&lt;tag&gt;", Strings.htmlEscape("<tag>"));
	}

	public void testOneStartsWith() {
		assertEquals("one", Strings.oneStartsWith(Arrays.asList("one", "two"), "one"));
		assertEquals("two", Strings.oneStartsWith(Arrays.asList("one", "two"), "two"));

		assertEquals("one", Strings.oneStartsWith(Arrays.asList("one", "two"), "one/more"));
		assertEquals("two", Strings.oneStartsWith(Arrays.asList("one", "two"), "twomore"));

		assertNull(Strings.oneStartsWith(Arrays.asList("one", "two"), "/one"));
		assertNull(Strings.oneStartsWith(Arrays.asList("one", "two"), " two"));

	}

	public void testIsUrlFriendly() {
		assertTrue(Strings.isUrlFriendly("www.abc"));
		assertTrue(Strings.isUrlFriendly("www.a_c"));
		assertTrue(Strings.isUrlFriendly("www.a_c.d_f"));
		assertTrue(Strings.isUrlFriendly("www.abc.def"));
		assertTrue(Strings.isUrlFriendly("www.abc.def/gh?a=1"));
		assertTrue(Strings.isUrlFriendly("http://www.abc.def/gh?a=1"));
		assertTrue(Strings.isUrlFriendly("tp://www.abc.def/gh?a=1"));
		assertFalse(Strings.isUrlFriendly("tp://ww$w.abc%.def/gh?a=1"));
		assertTrue(Strings.isUrlFriendly("one.two"));
		assertFalse(Strings.isUrlFriendly(""));
		assertFalse(Strings.isUrlFriendly("oneitem"));
		assertFalse(Strings.isUrlFriendly("asd$l.......jkal---s_____dj"));
	}

	public void testSegmentFn() {
		checkSegmentFn(0, "a", "a/b/c");
		checkSegmentFn(1, "b", "a/b/c");
		checkSegmentFn(2, "c", "a/b/c");
		checkSegmentFn(3, "", "a/b/c");

		checkSegmentFn(0, "a", "/a/b/c");
		checkSegmentFn(1, "b", "/a/b/c");
		checkSegmentFn(2, "c", "/a/b/c");
		checkSegmentFn(3, "", "/a/b/c");

		checkSegmentFn(0, "a", "/a///b///c");
		checkSegmentFn(1, "b", "/a///b///c");
		checkSegmentFn(2, "c", "/a///b///c//");
		checkSegmentFn(3, "", "///a/b/c//");

		checkSegmentFn(0, "abc", "abc");
		checkSegmentFn(1, "", "abc");

		checkSegmentFn(0, "", "");
		checkSegmentFn(1, "", "");

		checkSegmentFn(0, null, null);
		checkSegmentFn(1, null, null);
	}

	private void checkSegmentFn(int i, String expected, String raw) {
		assertEquals(expected, Strings.segment(raw, "/", i));
		assertEquals(expected, Functions.call(Strings.segmentFn("/", i), raw));

	}

	public void testRemoveBrackets() {
		assertEquals("onethree", Strings.removeBrackets("one<two>three", '<', '>'));
		assertEquals("one<two>three", Strings.removeBrackets("one<two>three", '{', '}'));
		assertEquals("onefive", Strings.removeBrackets("one<two<three>four>five", '<', '>'));
		assertEquals("", Strings.removeBrackets("", '<', '>'));
		assertEquals(null, Strings.removeBrackets(null, '<', '>'));
	}

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

	public void testVersions() {
		checkVersions("\\a\\b\\c\\spring-code-1.0.0.jar", "spring-code", "1.0.0");
		checkVersions("a/b/c/spring-code-1.0.0.jar", "spring-code", "1.0.0");
		checkVersions("spring-code-1.0.0.jar", "spring-code", "1.0.0");
		checkVersions("spring-code-1.A.0.jar", "n/a", "n/a");
		checkVersions("spring-code-1.A.0.jar", "n/a", "n/a");
		checkVersions(null, "n/a", "n/a");
	}

	private void checkVersions(String raw, String prefix, String version) {
		File file = raw == null ? null : new File(raw);
		assertEquals(version, Strings.versionPartOf(file, "n/a"));
		assertEquals(prefix, Strings.withoutVersion(file, "n/a"));
	}

	public void testLastSegmentAndAllButLastSegment() {
		checkLastSegment(null, null, null);
		checkLastSegment("", "", "");
		checkLastSegment("abc", "abc", "abc");
		checkLastSegment("/a/b", "/a", "b");
		checkLastSegment("/a/b/c", "/a/b", "c");
	}

	private void checkLastSegment(String input, String begin, String end) {
		assertEquals(end, Strings.lastSegment(input, "/"));
		assertEquals(end, Functions.call(Strings.lastSegmentFn("/"), input));
		assertEquals(begin, Strings.allButLastSegment(input, "/"));

	}

	public void testFirstSegment() {
		assertEquals("a", Strings.firstSegment("a.b.c", "."));
		assertEquals("a.b.c", Strings.firstSegment("a.b.c", ":"));
		assertEquals(null, Strings.firstSegment(null, "."));

	}

	public void testLowerAndUpperCaseFirstCharacter() {
		checkLowerAndUpperCaseFirstCharacter("Abc", "abc", "Abc");
		checkLowerAndUpperCaseFirstCharacter("Abc", "abc", "abc");
		checkLowerAndUpperCaseFirstCharacter("ABc", "aBc", "aBc");
		checkLowerAndUpperCaseFirstCharacter("A", "a", "a");
		checkLowerAndUpperCaseFirstCharacter("", "", "");
		checkLowerAndUpperCaseFirstCharacter(null, null, null);
	}

	private void checkLowerAndUpperCaseFirstCharacter(String expectedUpper, String expectedLower, String raw) {
		assertEquals(expectedLower, Strings.lowerCaseFirstCharacter(raw));
		assertEquals(expectedUpper, Strings.upperCaseFirstCharacter(raw));
		assertEquals(expectedUpper, Functions.call(Strings.upperCaseFirstCharacterFn(), raw));

	}

	public void testVersionCompare() {
		assertEquals(0, Strings.compareVersionNumbers("1.2.1", "1.2.1"));
		assertEquals(1, Strings.compareVersionNumbers("1.2.2", "1.2.1"));
		assertEquals(1, Strings.compareVersionNumbers("1.3.1", "1.2.1"));
		assertEquals(1, Strings.compareVersionNumbers("2.1.1", "1.2.1"));

		assertEquals(-1, Strings.compareVersionNumbers("1.2", "1.2.1"));
		assertEquals(1, Strings.compareVersionNumbers("1.2.1", "1.2"));

		assertEquals(0, Strings.compareVersionNumbers("1.2.11", "1.2.11"));
		assertEquals(-9, Strings.compareVersionNumbers("1.2.2", "1.2.11"));
		assertEquals(-111, Strings.compareVersionNumbers("1.2.11", "1.113.1"));

		assertEquals(0, Strings.compareVersionNumbers("1-2-11", "1.2.11"));
		assertEquals(-9, Strings.compareVersionNumbers("1-2-2", "1.2.11"));
		assertEquals(-111, Strings.compareVersionNumbers("1-2-11", "1.113.1"));

		assertEquals(0, Strings.compareVersionNumbers("1-2-11", "1-2-11"));
		assertEquals(-9, Strings.compareVersionNumbers("1-2-2", "1-2-11"));
		assertEquals(-111, Strings.compareVersionNumbers("1-2-11", "1-113-1"));
	}

	public void testSqlEscape() {
		assertEquals("abc", Strings.sqlEscape("abc"));
		assertEquals("a''bc", Strings.sqlEscape("a'bc"));
		assertEquals("a''''bc", Strings.sqlEscape("a''bc"));
		assertEquals("a''bc", Strings.sqlEscape("a\'bc"));
		assertEquals("a\\''bc", Strings.sqlEscape("a\\'bc"));
	}

	public void testKeepOnly() {
		assertEquals("1234", Strings.onlyKeep("raw12and3,4", "4231"));
	}

	public void testLastSegmentFn() throws Exception {
		assertEquals("whole", Strings.lastSegmentFn("/").apply("whole"));
		assertEquals("last", Strings.lastSegmentFn("/").apply("first/second/last"));
	}

}
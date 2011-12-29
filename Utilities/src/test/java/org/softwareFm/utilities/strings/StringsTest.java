/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.strings;

import java.io.File;

import junit.framework.TestCase;

import org.softwareFm.utilities.functions.Functions;

public class StringsTest extends TestCase {
	
	public void testIsUrlFriendly(){
		assertTrue(Strings.isUrlFriendly("asdljkalsdj"));
		assertTrue(Strings.isUrlFriendly("asdljkal---s_____dj"));
		assertTrue(Strings.isUrlFriendly("asdl.......jkal---s_____dj"));
		assertFalse(Strings.isUrlFriendly(""));
		assertFalse(Strings.isUrlFriendly("asd$l.......jkal---s_____dj"));
	}
	
	public void testRemoveBrackets(){
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
	public void testLastSegmentAndAllButLastSegment(){
		checkLastSegment(null, null,null);
		checkLastSegment("", "","");
		checkLastSegment("abc", "abc","abc");
		checkLastSegment("/a/b", "/a","b");
		checkLastSegment("/a/b/c", "/a/b","c");
	}
	
	private void checkLastSegment(String input, String begin, String end) {
		assertEquals(end, Strings.lastSegment(input, "/"));
		assertEquals(end, Functions.call(Strings.lastSegmentFn("/"),input));
		assertEquals(begin, Strings.allButLastSegment(input, "/"));
		
	}
	
	public void testFirstSegment(){
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
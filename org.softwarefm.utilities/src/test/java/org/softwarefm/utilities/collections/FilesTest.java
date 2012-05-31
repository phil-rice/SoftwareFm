/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */


package org.softwarefm.utilities.collections;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

public class FilesTest extends TestCase {

	public void testDefaultMimeType() {
		checkDefaultMimeType("text/html", "abc.html");
		checkDefaultMimeType("image/png", "abc.png");
		checkDefaultMimeType("image/gif", "abc.gif");
		checkDefaultMimeType("image/jpg", "abc.jpg");
		checkDefaultMimeType("text/xml", "abc.xml");
		checkDefaultMimeType("application/java-archive", "abc.jar");
		checkDefaultMimeType("text/plain", "abc.unknown");
	}

	private void checkDefaultMimeType(String expected, String fileName) {
		assertEquals(expected, Files.defaultMimeType(fileName));

	}

	@Test
	public void testNameWithoutExtension() {
		assertEquals("Fred", Files.nameWithoutExtension(new File("a.b.c/W/Fred.class")));
		assertEquals("Fred", Files.nameWithoutExtension(new File("a.b.c/W/Fred.a.b.class")));
		assertEquals("Fred", Files.nameWithoutExtension(new File("Fred.a.b.class")));
	}

	@Test
	public void testNoExtension() {
		assertEquals("Fred.a.b", Files.noExtension("Fred.a.b.class"));
		assertEquals("a.b.c/W/Fred", Files.noExtension("a.b.c/W/Fred.class"));
		assertEquals("a.b.c/W/Fred.a.b", Files.noExtension("a.b.c/W/Fred.a.b.class"));
	}

	public void testExtension() {
		assertEquals("class", Files.extension("Fred.a.b.class"));
		assertEquals("class", Files.extension("a.b.c/W/Fred.class"));
		assertEquals("class", Files.extension("a.b.c/W/Fred.a.b.class"));
	}

	public static void main(String[] args) throws IOException {
		for (File file : Files.walkChildrenOf(new File("."), Files.extensionFilter("java")))
			System.out.println(file.getCanonicalPath());
	}
}
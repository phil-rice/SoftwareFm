/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.collections;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class FilesTest extends TestCase {

	
	@Test
	public void testDigest() throws IOException {
		ClassPathResource resource = new ClassPathResource("UnchangingFile.txt", getClass());
		InputStream inputStream = resource.getInputStream();
		byte[] digest1 = Files.digest(inputStream);
		byte[] digest2 = Files.digest(resource.getFile());
		assertTrue(Arrays.equals(digest1, digest2));
		assertTrue(Lists.asList(digest2).toString(), Arrays.equals(new byte[] { 52, -40, 102, -11, 21, 8, 98, -19, -24, 55, 11, -102, 59, -88, 63, -123, 77, -11, -32, 14 }, digest2));
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
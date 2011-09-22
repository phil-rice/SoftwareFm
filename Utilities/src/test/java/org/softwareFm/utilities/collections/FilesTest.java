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

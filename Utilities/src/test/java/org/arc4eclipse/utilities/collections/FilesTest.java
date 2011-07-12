package org.arc4eclipse.utilities.collections;

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
		assertTrue(Arrays.equals(new byte[] { 5, 46, -78, -74, -122, 112, 88, 73, 56, -63, 109, -55, 87, 74, -9, 90, -99, -90, -47, -57 }, digest2));
	}
}

package org.arc4eclipse.repositoryClient.api;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class IJarDetailsTest extends TestCase {

	@Test
	public void testJarDetails() throws IOException {
		File file = new ClassPathResource("AnotherUnchangingFile.txt", getClass()).getFile();
		IJarDetails initialDetails = IJarDetails.Utils.makeJarDetails(file, "releaseText");
		IJarDetails withDigest1 = IJarDetails.Utils.withDigest(initialDetails);
		IJarDetails withDigest2 = IJarDetails.Utils.withDigest(withDigest1);

		assertSame(file, initialDetails.pathToJar());
		assertSame(file, withDigest1.pathToJar());

		assertEquals("releaseText", initialDetails.release());
		assertEquals("releaseText", withDigest1.release());

		assertNull(initialDetails.digest());
		assertTrue(Arrays.equals(new byte[] { 106, -62, 49, -31, 40, 91, 99, -123, -32, -67, -82, 19, -78, 5, 35, -19, 83, 10, 17, 0 }, withDigest2.digest()));

		assertEquals("null", initialDetails.digestAsHexString());
		assertEquals("6ac231e1285b6385e0bdae13b20523ed530a1100", withDigest1.digestAsHexString());

		assertSame(withDigest1, withDigest2);
	}
}

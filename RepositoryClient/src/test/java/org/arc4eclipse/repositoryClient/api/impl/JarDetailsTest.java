package org.arc4eclipse.repositoryClient.api.impl;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

public class JarDetailsTest extends TestCase {

	@Test
	public void testJarDetails() {
		checkJarDetails("c.txt", "null", "a/b/c.txt", null, null);
		checkJarDetails("c.txt", "10203", "a/b/c.txt", new byte[] { 1, 2, 3 }, "rel");
		checkJarDetails("c.txt", "10203", "c.txt", new byte[] { 1, 2, 3 }, "rel");
	}

	private void checkJarDetails(String expectedShortName, String expectedDigestAsHex, String fileSpec, byte[] digest, String release) {
		File pathToJar = new File(fileSpec);
		JarDetails jarDetails = new JarDetails(pathToJar, digest, release);
		assertSame(digest, jarDetails.digest());
		assertEquals(expectedDigestAsHex, jarDetails.digestAsHexString());
		assertEquals(pathToJar, jarDetails.pathToJar());
		assertEquals(expectedShortName, jarDetails.shortJarName());
	}

}

package org.arc4eclipse.utilities.strings;

import junit.framework.TestCase;

import org.junit.Test;

public class UrlsTest extends TestCase {

	@Test
	public void test() {
		checkUrlRipper("http://a.b.c/wibble.html", "http", "a.b.c/wibble", "html");
		checkUrlRipper("a.b.c/wibble.html", "", "a.b.c/wibble", "html");
		checkUrlRipper("a.b.c/wibble", "", "a.b.c/wibble", "");
	}

	private void checkUrlRipper(String url, String protocol, String resourcePath, String extension) {
		UrlRipperResult result = Urls.rip(url);
		assertEquals(url, result.url);
		assertEquals(protocol, result.protocol);
		assertEquals(resourcePath, result.resourcePath);
		assertEquals(extension, result.extension);
	}

}

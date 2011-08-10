package org.arc4eclipse.utilities.strings;

import java.net.URI;

import junit.framework.TestCase;

import org.junit.Test;

public class UrlsTest extends TestCase {

	@Test
	public void testRipper() {
		checkUrlRipper("http://a.b.c/wibble.html", "http", "a.b.c/wibble", "html");
		checkUrlRipper("a.b.c/wibble.html", "", "a.b.c/wibble", "html");
		checkUrlRipper("a.b.c/wibble", "", "a.b.c/wibble", "");
	}

	public void testWithDefaultProtocol() {
		checkWithDefaultProtocol("http://a", "a");
		checkWithDefaultProtocol("http://a", "http://a");
		checkWithDefaultProtocol("http://a.com", "a.com");
		checkWithDefaultProtocol("http://a.com", "http://a.com");
		checkWithDefaultProtocol("http://a.b.com", "a.b.com");
		checkWithDefaultProtocol("http://a.b.com", "http://a.b.com");
	}

	private void checkWithDefaultProtocol(String expected, String raw) {
		URI actual = Urls.withDefaultProtocol("http", raw);
		assertEquals(expected, actual.toString());

	}

	private void checkUrlRipper(String url, String protocol, String resourcePath, String extension) {
		UrlRipperResult result = Urls.rip(url);
		assertEquals(url, result.url);
		assertEquals(protocol, result.protocol);
		assertEquals(resourcePath, result.resourcePath);
		assertEquals(extension, result.extension);
	}

}

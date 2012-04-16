/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.strings;

import java.net.URI;
import java.util.Arrays;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.strings.UrlRipperResult;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class UrlsTest extends TestCase {

	public void testUrlsToRoot(){
		checkUrlToRoot(null);
		checkUrlToRoot("");
		checkUrlToRoot("a", "a");
		checkUrlToRoot("a/b/c", "a", "a/b", "a/b/c");
		checkUrlToRoot("ab/cd/ef", "ab", "ab/cd", "ab/cd/ef");
	}
	
	private void checkUrlToRoot(String url, String ...expected) {
		assertEquals(Arrays.asList(expected), Urls.urlsToRoot(url));
	}

	public void testComposeFirst() {
		assertEquals("", Urls.composeFirst(0, "a", "b"));
		assertEquals("a", Urls.composeFirst(1, "a", "b"));
		assertEquals("a/b", Urls.composeFirst(2, "a", "b"));
	}
	public void testCompose() {
		assertEquals("a/b", Urls.compose("a", "b"));
		assertEquals("a/b", Urls.compose("a/", "b"));
		assertEquals("a/b", Urls.compose("a/", "/b"));
		assertEquals("/a/b", Urls.composeWithSlash("a", "b"));
		assertEquals("/a/b", Urls.composeWithSlash("a/", "b"));
		assertEquals("/a/b", Urls.composeWithSlash("a/", "/b"));
	}

	public void testRipper() {
		checkUrlRipper("http://a.b.c/wibble.html", "http", "a.b.c/wibble", "html");
		checkUrlRipper("a.b.c/wibble.html", "", "a.b.c/wibble", "html");
		checkUrlRipper("a.b.c/wibble", "", "a.b.c/wibble", "");
	}

	public void testWithNull() {
		checkUrlRipper("", "", "", "");
		checkUrlRipper(null, "", "", "");

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
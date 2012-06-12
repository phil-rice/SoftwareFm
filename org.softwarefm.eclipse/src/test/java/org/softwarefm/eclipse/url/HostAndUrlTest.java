package org.softwarefm.eclipse.url;

import junit.framework.TestCase;

public class HostAndUrlTest extends TestCase {

	public void testWithFragments() {
		HostAndUrl hostAndUrl = new HostAndUrl("host", "f1", "f2");
		assertEquals("host", hostAndUrl.host);
		assertEquals("f1/f2", hostAndUrl.url);
		assertEquals("host/f1/f2", hostAndUrl.getHostAndUrl());
	}

	public void testWithOutFragments() {
		HostAndUrl hostAndUrl = new HostAndUrl("host");
		assertEquals("host", hostAndUrl.host);
		assertEquals("", hostAndUrl.url);
		assertEquals("host", hostAndUrl.getHostAndUrl());

	}

}

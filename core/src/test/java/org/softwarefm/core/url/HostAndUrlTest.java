package org.softwarefm.core.url;

import junit.framework.TestCase;

public class HostAndUrlTest extends TestCase {

	public void testWithFragments() {
		HostOffsetAndUrl hostOffsetAndUrl = new HostOffsetAndUrl("host", "offset", "f1", "f2");
		assertEquals("host", hostOffsetAndUrl.host);
		assertEquals("offset", hostOffsetAndUrl.offset);
		assertEquals("f1/f2", hostOffsetAndUrl.url);
		assertEquals("host/offset/f1/f2", hostOffsetAndUrl.getHostAndUrl());
	}

	public void testWithOutFragments() {
		HostOffsetAndUrl hostOffsetAndUrl = new HostOffsetAndUrl("host", "offset");
		assertEquals("host", hostOffsetAndUrl.host);
		assertEquals("offset", hostOffsetAndUrl.offset);
		assertEquals("", hostOffsetAndUrl.url);
		assertEquals("host/offset", hostOffsetAndUrl.getHostAndUrl());

	}

}

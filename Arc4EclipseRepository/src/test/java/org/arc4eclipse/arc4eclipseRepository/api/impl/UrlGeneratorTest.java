package org.arc4eclipse.arc4eclipseRepository.api.impl;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.junit.Test;

public class UrlGeneratorTest extends TestCase {

	private IUrlGenerator urlGenerator;

	@Test
	public void testForJar() {
		assertEquals("/aTh/aThisIsDigest/jar", urlGenerator.forJar("ThisIsDigest"));
	}

	public void testForOrganisation() {
		assertEquals(565, Math.abs("someurl".hashCode() % 1000));
		assertEquals("/565/someurl/organisation", urlGenerator.forOrganisation("someUrl"));
		assertEquals("/565/someurl/organisation", urlGenerator.forOrganisation("@#'~someUrl?"));
		assertEquals("/472/someurlabd/organisation", urlGenerator.forOrganisation("@#'~someUrl/?abd"));
	}

	public void testForProject() {
		assertEquals("/565/someurl/organisation/proj1/project", urlGenerator.forProject("someUrl", "proj1"));
	}

	public void testForRelease() {
		assertEquals("/565/someurl/organisation/proj1/project/rel1/release", urlGenerator.forRelease("someUrl", "proj1", "rel1"));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		urlGenerator = new UrlGenerator();
	}
}

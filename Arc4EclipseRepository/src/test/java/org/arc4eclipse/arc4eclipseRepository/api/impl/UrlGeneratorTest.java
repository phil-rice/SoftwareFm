package org.arc4eclipse.arc4eclipseRepository.api.impl;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;

public class UrlGeneratorTest extends TestCase {

	private IUrlGenerator urlGenerator;

	public void testForJar() {
		assertEquals("/jars/aTh/aThisIsDigest", urlGenerator.forJar("ThisIsDigest"));
	}

	public void testForOrganisation() {
		assertEquals(565, Math.abs("someurl".hashCode() % 1000));
		assertEquals("/organisations/565/someurl", urlGenerator.forOrganisation("someUrl"));
		assertEquals("/organisations/565/someurl", urlGenerator.forOrganisation("@#'~someUrl?"));
		assertEquals("/organisations/472/someurlabd", urlGenerator.forOrganisation("@#'~someUrl/?abd"));
	}

	public void testForProject() {
		assertEquals(532, Math.abs("proj1".hashCode() % 1000));
		assertEquals("/projects/532/proj1", urlGenerator.forProject("someUrl", "proj1"));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		urlGenerator = new UrlGenerator();
	}
}

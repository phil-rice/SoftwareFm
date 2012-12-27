package org.softwarefm.core.templates.internal;

import junit.framework.TestCase;

import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.utilities.tests.IIntegrationTest;

public class TemplateStoreTest extends TestCase implements IIntegrationTest {

	public void testTemplate() {
		TemplateStore templateStore = new TemplateStore(IUrlStrategy.Utils.urlStrategy());
		
		assertFalse(templateStore.cache.containsKey("testTemplate"));
		assertEquals("Testing\nTesting\n123\n", templateStore.getTemplate("testTemplate"));
		assertEquals("Testing\nTesting\n123\n", templateStore.getTemplate("testTemplate"));

		assertTrue(templateStore.cache.containsKey("testTemplate"));
	}
	

}

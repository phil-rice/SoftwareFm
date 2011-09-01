package org.arc4eclipse.core.plugin;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;

public abstract class AbstractUrlGeneratorTest extends TestCase {

	abstract protected String getUrlGeneratorName();

	abstract protected String getRawUrl();

	abstract protected String getExpected();

	public void testUrlWasCreated() throws Exception {
		IUrlGenerator generator = Arc4EclipseCoreActivator.getDefault().getUrlGeneratorMap().get(getUrlGeneratorName());
		assertNotNull(generator);
		assertEquals(getExpected(), generator.apply(getRawUrl()));
	}

}

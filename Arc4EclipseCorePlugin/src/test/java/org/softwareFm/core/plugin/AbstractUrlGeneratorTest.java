package org.softwareFm.core.plugin;

import junit.framework.TestCase;

import org.softwareFm.arc4eclipseRepository.api.IUrlGenerator;
import org.softwareFm.core.plugin.Arc4EclipseCoreActivator;

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

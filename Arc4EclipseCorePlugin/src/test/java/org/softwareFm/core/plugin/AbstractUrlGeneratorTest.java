package org.softwareFm.core.plugin;

import junit.framework.TestCase;

import org.softwareFm.core.plugin.SoftwareFmActivator;
import org.softwareFm.repository.api.IUrlGenerator;

public abstract class AbstractUrlGeneratorTest extends TestCase {

	abstract protected String getUrlGeneratorName();

	abstract protected String getRawUrl();

	abstract protected String getExpected();

	public void testUrlWasCreated() throws Exception {
		IUrlGenerator generator = SoftwareFmActivator.getDefault().getUrlGeneratorMap().get(getUrlGeneratorName());
		assertNotNull(generator);
		assertEquals(getExpected(), generator.apply(getRawUrl()));
	}

}

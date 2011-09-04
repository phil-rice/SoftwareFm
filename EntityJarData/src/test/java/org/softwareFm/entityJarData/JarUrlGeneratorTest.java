package org.softwareFm.entityJarData;

import org.softwareFm.core.plugin.AbstractUrlGeneratorTest;
import org.softwareFm.repository.constants.RepositoryConstants;


public class JarUrlGeneratorTest extends AbstractUrlGeneratorTest {

	@Override
	protected String getUrlGeneratorName() {
		return RepositoryConstants.entityJar;
	}

	@Override
	protected String getRawUrl() {
		return "someJarDigest";
	}

	@Override
	protected String getExpected() {
		return "/jars/595/somejardigest";
	}

}

package org.softwareFm.entityJarData;

import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;
import org.softwareFm.core.plugin.AbstractUrlGeneratorTest;


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

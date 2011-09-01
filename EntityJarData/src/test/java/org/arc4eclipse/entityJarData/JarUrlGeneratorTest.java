package org.arc4eclipse.entityJarData;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.core.plugin.AbstractUrlGeneratorTest;

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

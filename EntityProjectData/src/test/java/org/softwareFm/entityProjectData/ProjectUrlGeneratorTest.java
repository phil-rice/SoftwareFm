package org.softwareFm.entityProjectData;

import org.softwareFm.core.plugin.AbstractUrlGeneratorTest;
import org.softwareFm.repository.constants.RepositoryConstants;


public class ProjectUrlGeneratorTest extends AbstractUrlGeneratorTest {

	@Override
	protected String getUrlGeneratorName() {
		return RepositoryConstants.entityProject;

	}

	@Override
	protected String getRawUrl() {
		return "www.some.org/x/y.z/q=?a=3";
	}

	@Override
	protected String getExpected() {
		return "/organisations/221/www.some.orgxy.zqa3";
	}

}

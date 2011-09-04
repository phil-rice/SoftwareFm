package org.softwareFm.entityOrganisationData;

import org.softwareFm.core.plugin.AbstractUrlGeneratorTest;
import org.softwareFm.repository.constants.RepositoryConstants;


public class OrganisationUrlGeneratorTest extends AbstractUrlGeneratorTest {

	@Override
	protected String getUrlGeneratorName() {
		return RepositoryConstants.entityOrganisation;

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

package org.arc4eclipse.entityOrganisationData;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.core.plugin.AbstractUrlGeneratorTest;

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

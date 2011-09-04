package org.softwareFm.entityOrganisationData;

import org.softwareFm.repository.api.IUrlGenerator;
import org.softwareFm.repository.constants.RepositoryConstants;


public class OrganisationUrlGenerator implements IUrlGenerator {

	@Override
	public String apply(String from) {
		return IUrlGenerator.Utils.makeUrl(from, RepositoryConstants.entityOrganisation);
	}

}

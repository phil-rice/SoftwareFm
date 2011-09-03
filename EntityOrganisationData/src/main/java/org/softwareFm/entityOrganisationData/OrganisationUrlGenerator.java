package org.softwareFm.entityOrganisationData;

import org.softwareFm.arc4eclipseRepository.api.IUrlGenerator;
import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;


public class OrganisationUrlGenerator implements IUrlGenerator {

	@Override
	public String apply(String from) {
		return IUrlGenerator.Utils.makeUrl(from, RepositoryConstants.entityOrganisation);
	}

}

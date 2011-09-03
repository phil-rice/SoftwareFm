package org.softwareFm.entityOrganisationData;

import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;
import org.softwareFm.displayCore.api.RepositoryStatusListenerPropogator;


public class OrganisationRepositoryStatusListenerPropogator extends RepositoryStatusListenerPropogator {

	public OrganisationRepositoryStatusListenerPropogator() {
		super(RepositoryConstants.entityJar, RepositoryConstants.entityOrganisation, "organisation.url");
	}

}

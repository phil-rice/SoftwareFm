package org.arc4eclipse.entityOrganisationData;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.RepositoryStatusListenerPropogator;

public class OrganisationRepositoryStatusListenerPropogator extends RepositoryStatusListenerPropogator {

	public OrganisationRepositoryStatusListenerPropogator() {
		super(RepositoryConstants.entityJar, RepositoryConstants.entityOrganisation, "organisation.url");
	}

}

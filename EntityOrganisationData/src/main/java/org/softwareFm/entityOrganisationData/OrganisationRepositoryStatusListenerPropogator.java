package org.softwareFm.entityOrganisationData;

import org.softwareFm.displayCore.api.RepositoryStatusListenerPropogator;
import org.softwareFm.repository.constants.RepositoryConstants;


public class OrganisationRepositoryStatusListenerPropogator extends RepositoryStatusListenerPropogator {

	public OrganisationRepositoryStatusListenerPropogator() {
		super(RepositoryConstants.entityJar, RepositoryConstants.entityOrganisation, "organisation.url");
	}

}

package org.softwareFm.entityOrganisationData;

import org.softwareFm.core.plugin.AbstractRepositoryStatusListenerPropogatorTest;
import org.softwareFm.displayCore.api.RepositoryStatusListenerPropogator;


public class OrganisationRepositoryStatusListenerPropogatorTest extends AbstractRepositoryStatusListenerPropogatorTest {

	@Override
	protected RepositoryStatusListenerPropogator getPropogator() {
		return new OrganisationRepositoryStatusListenerPropogator();
	}

}

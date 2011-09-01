package org.arc4eclipse.entityOrganisationData;

import org.arc4eclipse.core.plugin.AbstractRepositoryStatusListenerPropogatorTest;
import org.arc4eclipse.displayCore.api.RepositoryStatusListenerPropogator;

public class OrganisationRepositoryStatusListenerPropogatorTest extends AbstractRepositoryStatusListenerPropogatorTest {

	@Override
	protected RepositoryStatusListenerPropogator getPropogator() {
		return new OrganisationRepositoryStatusListenerPropogator();
	}

}

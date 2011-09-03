package org.softwareFm.entityProjectData;

import org.softwareFm.core.plugin.AbstractRepositoryStatusListenerPropogatorTest;
import org.softwareFm.displayCore.api.RepositoryStatusListenerPropogator;


public class ProjectRepositoryStatusListenerPropogatorTest extends AbstractRepositoryStatusListenerPropogatorTest {

	@Override
	protected RepositoryStatusListenerPropogator getPropogator() {
		return new ProjectRepositoryStatusListenerPropogator();
	}

}

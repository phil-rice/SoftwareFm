package org.arc4eclipse.entityProjectData;

import org.arc4eclipse.core.plugin.AbstractRepositoryStatusListenerPropogatorTest;
import org.arc4eclipse.displayCore.api.RepositoryStatusListenerPropogator;
import org.arc4eclipse.entityProjectData.ProjectRepositoryStatusListenerPropogator;

public class ProjectRepositoryStatusListenerPropogatorTest extends AbstractRepositoryStatusListenerPropogatorTest {

	@Override
	protected RepositoryStatusListenerPropogator getPropogator() {
		return new ProjectRepositoryStatusListenerPropogator();
	}

}

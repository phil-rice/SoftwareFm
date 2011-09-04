package org.softwareFm.entityProjectData;

import org.softwareFm.displayCore.api.RepositoryStatusListenerPropogator;
import org.softwareFm.repository.constants.RepositoryConstants;


public class ProjectRepositoryStatusListenerPropogator extends RepositoryStatusListenerPropogator {

	public ProjectRepositoryStatusListenerPropogator() {
		super(RepositoryConstants.entityJar, RepositoryConstants.entityProject, "project.url");
	}

}

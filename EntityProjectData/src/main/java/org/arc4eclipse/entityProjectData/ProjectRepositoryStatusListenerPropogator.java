package org.arc4eclipse.entityProjectData;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.RepositoryStatusListenerPropogator;

public class ProjectRepositoryStatusListenerPropogator extends RepositoryStatusListenerPropogator {

	public ProjectRepositoryStatusListenerPropogator() {
		super(RepositoryConstants.entityJar, RepositoryConstants.entityProject, "project.url");
	}

}

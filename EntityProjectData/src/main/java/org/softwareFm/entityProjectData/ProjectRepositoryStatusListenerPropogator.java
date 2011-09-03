package org.softwareFm.entityProjectData;

import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;
import org.softwareFm.displayCore.api.RepositoryStatusListenerPropogator;


public class ProjectRepositoryStatusListenerPropogator extends RepositoryStatusListenerPropogator {

	public ProjectRepositoryStatusListenerPropogator() {
		super(RepositoryConstants.entityJar, RepositoryConstants.entityProject, "project.url");
	}

}

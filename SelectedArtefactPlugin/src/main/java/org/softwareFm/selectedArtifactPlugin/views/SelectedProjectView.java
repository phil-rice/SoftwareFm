package org.softwareFm.selectedArtifactPlugin.views;

import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;

public class SelectedProjectView extends AbstractSelectedArtifactView {
	@Override
	protected String getViewName() {
		return RepositoryConstants.viewProject;
	}

	@Override
	protected String getEntityName() {
		return RepositoryConstants.entityProject;
	}
}

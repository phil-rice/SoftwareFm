package org.softwareFm.selectedArtifactPlugin.views;

import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;

public class SelectedJarSummaryView extends AbstractSelectedArtifactView {

	@Override
	protected String getViewName() {
		return RepositoryConstants.viewSummaryJar;
	}

	@Override
	protected String getEntityName() {
		return RepositoryConstants.entityJar;
	}

}

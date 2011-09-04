package org.softwareFm.selectedArtifactPlugin.views;

import org.softwareFm.repository.constants.RepositoryConstants;

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

package org.softwareFm.selectedArtifactPlugin.views;

import org.softwareFm.repository.constants.RepositoryConstants;

public class SelectedJarView extends AbstractSelectedArtifactView {

	@Override
	protected String getViewName() {
		return RepositoryConstants.viewJar;
	}

	@Override
	protected String getEntityName() {
		return RepositoryConstants.entityJar;
	}

}

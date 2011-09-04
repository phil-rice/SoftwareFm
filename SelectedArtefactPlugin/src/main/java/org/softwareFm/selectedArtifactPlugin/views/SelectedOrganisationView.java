package org.softwareFm.selectedArtifactPlugin.views;

import org.softwareFm.repository.constants.RepositoryConstants;

public class SelectedOrganisationView extends AbstractSelectedArtifactView {
	@Override
	protected String getViewName() {
		return RepositoryConstants.viewOrganisation;
	}

	@Override
	protected String getEntityName() {
		return RepositoryConstants.entityOrganisation;
	}
}

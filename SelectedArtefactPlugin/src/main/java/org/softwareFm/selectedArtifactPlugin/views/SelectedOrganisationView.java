package org.softwareFm.selectedArtifactPlugin.views;

import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;


public class SelectedOrganisationView extends AbstractSelectedArtifactView {
	@Override
	protected String entity() {
		return RepositoryConstants.entityOrganisation;
	}
}

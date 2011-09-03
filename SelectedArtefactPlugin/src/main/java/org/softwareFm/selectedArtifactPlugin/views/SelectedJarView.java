package org.softwareFm.selectedArtifactPlugin.views;

import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;


public class SelectedJarView extends AbstractSelectedArtifactView {

	@Override
	protected String entity() {
		return RepositoryConstants.entityJar;
	}

}

package org.arc4eclipse.selectedArtifactPlugin.views;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;

public class SelectedJarView extends AbstractSelectedArtifactView {

	@Override
	protected String entity() {
		return RepositoryConstants.entityJar;
	}

}

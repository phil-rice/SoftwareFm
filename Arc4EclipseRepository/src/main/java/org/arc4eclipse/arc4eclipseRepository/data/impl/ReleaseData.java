package org.arc4eclipse.arc4eclipseRepository.data.impl;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IReleaseData;

public class ReleaseData extends AbstractRepositoryDataItem implements IReleaseData {

	public ReleaseData(Map<String, Object> data) {
		super(data);
	}

	@Override
	public String getOrganisationUrl() {
		return getString(Arc4EclipseRepositoryConstants.organisationUrlKey);
	}

	@Override
	public String getDescription() {
		return getString(Arc4EclipseRepositoryConstants.descriptionKey);
	}

	@Override
	public String getProjectName() {
		return getString(Arc4EclipseRepositoryConstants.projectNameKey);
	}

	@Override
	public String getReleaseIdentifier() {
		return getString(Arc4EclipseRepositoryConstants.releaseIdentifierKey);
	}

}

package org.arc4eclipse.arc4eclipseRepository.data.impl;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;

public class OrganisationData extends AbstractRepositoryDataItem implements IOrganisationData {

	public OrganisationData(Map<String, Object> data) {
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
	public String getOrganisationName() {
		return getString(Arc4EclipseRepositoryConstants.organisationNameKey);
	}
}

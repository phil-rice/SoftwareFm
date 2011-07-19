package org.arc4eclipse.arc4eclipseRepository.data.impl;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;

public class ProjectData extends AbstractRepositoryDataItem implements IProjectData {

	public ProjectData(Map<String, Object> data) {
		super(data);
	}

	@Override
	public String getDescription() {
		return getString(Arc4EclipseRepositoryConstants.descriptionKey);
	}

	@Override
	public String getOrganisationUrl() {
		return getString(Arc4EclipseRepositoryConstants.organisationUrlKey);
	}

	@Override
	public String getProjectName() {
		return getString(Arc4EclipseRepositoryConstants.projectNameKey);
	}

}

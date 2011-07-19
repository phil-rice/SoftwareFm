package org.arc4eclipse.arc4eclipseRepository.data.impl;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;

public class JarData extends AbstractRepositoryDataItem implements IJarData {

	public JarData(Map<String, Object> data) {
		super(data);
	}

	@Override
	public String getOrganisationUrl() {
		return getString(Arc4EclipseRepositoryConstants.organisationUrlKey);
	}

	@Override
	public String getReleaseIdentifier() {
		return getString(Arc4EclipseRepositoryConstants.releaseIdentifierKey);
	}

	@Override
	public String getHexDigest() {
		return getString(Arc4EclipseRepositoryConstants.hexDigest);
	}

	@Override
	public String getProjectName() {
		return getString(Arc4EclipseRepositoryConstants.projectNameKey);
	}

}

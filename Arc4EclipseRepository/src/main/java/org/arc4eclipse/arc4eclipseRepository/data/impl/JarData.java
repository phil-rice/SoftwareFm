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
	public String getHexDigest() {
		return getString(Arc4EclipseRepositoryConstants.hexDigestKey);
	}

	@Override
	public String getProjectUrl() {
		return getString(Arc4EclipseRepositoryConstants.projectUrlKey);
	}

	@Override
	public String getJavaDocUrl() {
		return getString(Arc4EclipseRepositoryConstants.javadocKey);
	}

	@Override
	public String getSourceUrl() {
		return getString(Arc4EclipseRepositoryConstants.sourceKey);
	}

}

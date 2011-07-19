package org.arc4eclipse.arc4eclipseRepository.data;

public interface IJarData extends IRepositoryDataItem {

	String getOrganisationUrl();

	String getReleaseIdentifier();

	String getHexDigest();

	String getProjectName();

}

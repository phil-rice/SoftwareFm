package org.arc4eclipse.arc4eclipseRepository.data;

public interface IReleaseData extends IRepositoryDataItem {
	String getOrganisationUrl();

	String getProjectName();

	String getReleaseIdentifier();

	String getDescription();

}

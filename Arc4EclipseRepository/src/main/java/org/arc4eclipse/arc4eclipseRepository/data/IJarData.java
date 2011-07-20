package org.arc4eclipse.arc4eclipseRepository.data;

public interface IJarData extends IRepositoryDataItem {

	String getOrganisationUrl();

	String getHexDigest();

	String getProjectName();

	String getJavaDocUrl();

	String getSourceUrl();

}

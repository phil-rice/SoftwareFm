package org.arc4eclipse.arc4eclipseRepository.data;

public interface IJarData extends IRepositoryDataItem {

	String getOrganisationUrl();

	String getHexDigest();

	String getProjectUrl();

	String getJavaDocUrl();

	String getSourceUrl();

}

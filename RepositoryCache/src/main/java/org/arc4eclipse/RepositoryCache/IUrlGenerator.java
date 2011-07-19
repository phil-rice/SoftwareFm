package org.arc4eclipse.RepositoryCache;

public interface IUrlGenerator {

	String forJar(String jarDigest);

	String forOrganisation(String organisationWebSiteUrl);

	String forProject(String organisationWebSiteUrl, String projectName);

	String forRelease(String organisationWebSiteUrl, String projectName, String releaseId);

}

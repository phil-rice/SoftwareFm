package org.arc4eclipse.arc4eclipseRepository.api;

public interface IUrlGenerator {

	String forJar(String jarDigest);

	String forOrganisation(String organisationUrl);

	String forProject(String organisationUrl, String projectUrl);

}

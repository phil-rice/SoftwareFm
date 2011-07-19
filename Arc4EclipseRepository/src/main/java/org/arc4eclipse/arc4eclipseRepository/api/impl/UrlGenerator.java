package org.arc4eclipse.arc4eclipseRepository.api.impl;

import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.utilities.strings.Strings;

public class UrlGenerator implements IUrlGenerator {

	@Override
	public String forJar(String jarDigest) {
		String prefix = jarDigest.substring(0, 2);
		return "/jars/a" + prefix + "/a" + jarDigest + "/jar";
	}

	@Override
	public String forOrganisation(String organisationUrl) {
		String cleanUrl = cleanUrl(organisationUrl);
		return "/organisations/" + Math.abs(cleanUrl.hashCode()) % 1000 + "/" + cleanUrl + "/organisation";
	}

	@Override
	public String forProject(String organisationUrl, String projectName) {
		return forOrganisation(organisationUrl) + "/" + cleanUrl(projectName) + "/project";
	}

	@Override
	public String forRelease(String organisationUrl, String projectName, String releaseIdentifier) {
		return forProject(organisationUrl, projectName) + "/" + cleanUrl(releaseIdentifier) + "/release";
	}

	private String cleanUrl(String organisationWebSiteUrl) {
		return Strings.onlyKeep(organisationWebSiteUrl.toLowerCase(), "abcdefghijklmnopqrstuvwxyz0123456789._");
	}

}

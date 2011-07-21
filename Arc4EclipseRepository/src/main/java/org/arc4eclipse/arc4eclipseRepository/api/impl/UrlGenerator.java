package org.arc4eclipse.arc4eclipseRepository.api.impl;

import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.utilities.strings.Strings;
import org.arc4eclipse.utilities.strings.UrlRipperResult;
import org.arc4eclipse.utilities.strings.Urls;

public class UrlGenerator implements IUrlGenerator {

	@Override
	public String forJar(String jarDigest) {
		if (jarDigest.length() < 2)
			return "";
		String prefix = jarDigest.substring(0, 2);
		return "/jars/a" + prefix + "/a" + jarDigest;
	}

	@Override
	public String forOrganisation(String organisationUrl) {
		return makeUrl(organisationUrl, "organisation");
	}

	@Override
	public String forProject(String organisationUrl, String projectUrl) {
		return makeUrl(projectUrl, "project");
	}

	private String makeUrl(String rawUrl, String name) {
		UrlRipperResult ripperResult = Urls.rip(rawUrl);
		String cleanUrl = Strings.onlyKeep(ripperResult.resourcePath.toLowerCase(), "abcdefghijklmnopqrstuvwxyz0123456789._");
		if (cleanUrl.length() < 2)
			return "";
		return "/" + name + "s/" + Math.abs(cleanUrl.hashCode()) % 1000 + "/" + cleanUrl;
	}

}

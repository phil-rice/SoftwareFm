package org.arc4eclipse.arc4eclipseRepository.api.impl;

import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.strings.Strings;
import org.arc4eclipse.utilities.strings.UrlRipperResult;
import org.arc4eclipse.utilities.strings.Urls;

public class UrlGenerator implements IUrlGenerator {

	@Override
	public IFunction1<String, String> forJar() {
		return new IFunction1<String, String>() {
			@Override
			public String apply(String jarDigest) throws Exception {
				if (jarDigest == null || jarDigest.length() < 2)
					return "";
				String prefix = jarDigest.substring(0, 2);
				return "/jars/a" + prefix + "/a" + jarDigest;
			}
		};
	}

	@Override
	public IFunction1<String, String> forOrganisation() {
		return new IFunction1<String, String>() {
			@Override
			public String apply(String organisationUrl) throws Exception {
				return makeUrl(organisationUrl, "organisation");
			}
		};
	}

	@Override
	public IFunction1<String, String> forProject() {
		return new IFunction1<String, String>() {
			@Override
			public String apply(String projectUrl) throws Exception {
				return makeUrl(projectUrl, "project");
			}
		};
	}

	private String makeUrl(String rawUrl, String name) {
		UrlRipperResult ripperResult = Urls.rip(rawUrl);
		String cleanUrl = Strings.onlyKeep(ripperResult.resourcePath.toLowerCase(), "abcdefghijklmnopqrstuvwxyz0123456789._");
		if (cleanUrl.length() < 2)
			return "";
		return "/" + name + "s/" + Math.abs(cleanUrl.hashCode()) % 1000 + "/" + cleanUrl;
	}

}

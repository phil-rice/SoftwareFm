package org.softwareFm.arc4eclipseRepository.api;

import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.strings.Strings;
import org.softwareFm.utilities.strings.UrlRipperResult;
import org.softwareFm.utilities.strings.Urls;

public interface IUrlGenerator extends IFunction1<String, String> {

	@Override
	public String apply(String from);

	public static class Utils {

		public static IUrlGenerator urlGenerator(final String name) {
			return new IUrlGenerator() {
				@Override
				public String apply(String from) {
					return makeUrl(from, name);
				}
			};
		}

		public static String makeUrl(String rawUrl, String name) {
			UrlRipperResult ripperResult = Urls.rip(rawUrl);
			String cleanUrl = Strings.onlyKeep(ripperResult.resourcePath.toLowerCase(), "abcdefghijklmnopqrstuvwxyz0123456789._");
			if (cleanUrl.length() < 2)
				return "";
			return "/" + name + "s/" + Math.abs(cleanUrl.hashCode()) % 1000 + "/" + cleanUrl;
		}
	}
}

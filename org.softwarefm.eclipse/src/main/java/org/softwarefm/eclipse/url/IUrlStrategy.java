package org.softwarefm.eclipse.url;

import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.url.internal.UrlStrategy;
import org.softwarefm.utilities.constants.CommonConstants;

public interface IUrlStrategy {

	String host();

	/** When reading with a browser this is appended after the hostname. Will often be 'wiki' or 'mediawiki/index.php' */
	String pageOffset();

	/** The media wiki api offset. This will often be mediawiki */
	String apiOffset();

	HostAndUrl classAndMethodUrl(ExpressionData expressionData);

	HostAndUrl digestUrl(String digest);

	HostAndUrl projectUrl(ProjectData projectData);

	HostAndUrl versionUrl(ProjectData projectData);

	public static class Utils {
		public static IUrlStrategy urlStrategy() {
			return new UrlStrategy(CommonConstants.softwareFmHost, CommonConstants.softwareFmPageOffset, CommonConstants.softwareFmApiOffset);
		}

		public static IUrlStrategy urlStrategy(String hostname) {
			return new UrlStrategy(hostname, CommonConstants.softwareFmPageOffset, CommonConstants.softwareFmApiOffset);

		}

		public static IUrlStrategy urlStrategy(String hostname, String pageOffset) {
			return new UrlStrategy(hostname, pageOffset, CommonConstants.softwareFmApiOffset);
		}

		public static IUrlStrategy urlStrategy(String hostname, String pageOffset, String apiOffset) {
			return new UrlStrategy(hostname, pageOffset, apiOffset);
		}
	}

}

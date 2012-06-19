package org.softwarefm.eclipse.url;

import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.url.internal.UrlStrategy;
import org.softwarefm.eclipse.url.internal.UrlStrategyWithActionBarState;
import org.softwarefm.utilities.constants.CommonConstants;

public interface IUrlStrategy {

	String host();

	/** When reading with a browser this is appended after the hostname. Will often be 'wiki' or 'mediawiki/index.php' */
	String pageOffset();

	/** The media wiki api offset. This will often be mediawiki */
	String apiOffset();

	HostOffsetAndUrl classAndMethodUrl(CodeData codeData);

	HostOffsetAndUrl digestUrl(String digest);

	HostOffsetAndUrl projectUrl(ArtifactData artifactData);

	HostOffsetAndUrl versionUrl(ArtifactData artifactData);

	HostOffsetAndUrl templateUrl(String name);

	public static class Utils {
		public static IUrlStrategy urlStrategy() {
			return new UrlStrategy(CommonConstants.softwareFmHost, CommonConstants.softwareFmPageOffset, CommonConstants.softwareFmApiOffset, CommonConstants.softwareFmTemplateOffset);
		}
		
		public static IUrlStrategy withActionBarState(IUrlStrategy urlStrategy, SfmActionState state){
			return new UrlStrategyWithActionBarState(urlStrategy, state);
		}

		public static IUrlStrategy urlStrategy(String hostname) {
			return new UrlStrategy(hostname, CommonConstants.softwareFmPageOffset, CommonConstants.softwareFmApiOffset, CommonConstants.softwareFmTemplateOffset);

		}

		public static IUrlStrategy urlStrategy(String hostname, String pageOffset) {
			return new UrlStrategy(hostname, pageOffset, CommonConstants.softwareFmApiOffset, CommonConstants.softwareFmTemplateOffset);
		}

		public static IUrlStrategy urlStrategy(String hostname, String pageOffset, String apiOffset) {
			return new UrlStrategy(hostname, pageOffset, apiOffset, CommonConstants.softwareFmTemplateOffset);
		}

		public static IUrlStrategy urlStrategy(String hostname, String pageOffset, String apiOffset, String templateOffset) {
			return new UrlStrategy(hostname, pageOffset, apiOffset, templateOffset);
		}
	}

}

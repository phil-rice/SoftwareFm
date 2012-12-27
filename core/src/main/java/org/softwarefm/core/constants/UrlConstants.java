package org.softwarefm.core.constants;

import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.strings.Strings;

public class UrlConstants {

	public static final String notJavaElementUrl = "http://" + Strings.url(CommonConstants.softwareFmHostAndPrefix, "notJavaElement");
	public static final String notJarUrl = "http://" + Strings.url(CommonConstants.softwareFmHostAndPrefix, "notJar");
	public static final String aboutArtifactComposite = "http://" + Strings.url(CommonConstants.softwareFmHostAndPrefix, "Help:ArtifactComposite");
	public static final String aboutCodeComposite = "http://" + Strings.url(CommonConstants.softwareFmHostAndPrefix, "Help:CodeComposite");

}

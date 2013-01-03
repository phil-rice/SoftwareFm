package org.softwarefm.utilities.constants;

import java.util.Arrays;
import java.util.List;

import org.softwarefm.utilities.strings.Strings;

public class CommonConstants {

	private static final boolean local = false;

	public static final long testTimeOutMs = 4000000;
	public static final long threadStayAliveTimeMs = 10000;
	public static final int maxThreadSize = 10;
	public static final int startThreadSize = 2;
	public static final int maxOutStandingJobs = 100;
	public static final String softwareFmHost = local ? "localhost" : "www.softwarefm.org";
	public static final String softwareFmPageOffset = local ? "wiki" : "mediawiki/index.php";
	public static final String softwareFmApiOffset = "mediawiki";
	public static final String softwareFmTemplateOffset = "template";
	public static final String softwareFmHostAndPrefix = Strings.url(softwareFmHost , softwareFmPageOffset);
	public static final List<Integer> okStatusCodes = Arrays.asList(200, 201);

}

package org.softwarefm.utilities.constants;

import java.util.Arrays;
import java.util.List;

public class CommonConstants {

	public static final long testTimeOutMs = 4000000;
	public static final long threadStayAliveTimeMs = 10000;
	public static final int maxThreadSize = 10;
	public static final int startThreadSize = 2;
	public static final int maxOutStandingJobs = 100;
	public static final String softwareFmHost = "localhost";
	public static final String softwareFmUrlPrefix = "/wiki/";
	public static final String softwareFmHostAndPrefix = softwareFmHost +  softwareFmUrlPrefix;
	public static final List<Integer> okStatusCodes = Arrays.asList(200, 201);

}

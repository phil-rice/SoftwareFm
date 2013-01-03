package org.softwarefm.shared.usage;

public class UsageConstants {

	public static final String host = "www.softwarefm.org";
	public static final int port = 8080;
	// public static final int updatePeriod = 5 * 1000; // 5 seconds
	// public static final int updatePeriod = 60 * 1000; // 1 minute1
	public static final int updatePeriod = 10 * 60 * 1000; // 10 mins
	// public static final int updatePeriod = 60 * 60 * 1000; // 1 hour
	public static final String preferencesNode = UsageConstants.class.getName();
	public static final String recordUsageKey = "record.usage";
	public static final String socialManagerFileName = "socialManager.xml";
}

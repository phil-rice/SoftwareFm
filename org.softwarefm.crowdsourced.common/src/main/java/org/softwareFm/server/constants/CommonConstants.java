package org.softwareFm.server.constants;

import java.util.Set;

import org.softwareFm.utilities.collections.Sets;

public class CommonConstants {

	public final static String dataFileName = "data.json";
	public static final String DOT_GIT = ".git";
	public static final String lockFileName = "lock.lck";

	public static final String typeTag = "sling:resourceType";
	public static final String collectionType = "collection";
	public static final String dataParameterName = "data";
	public static final String repoUrlKey = "repoUrl";

	public static final long testTimeOutMs = 500;

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";
	public static final String HEAD = "HEAD";

	public static final String findRepositoryBasePrefix = "command/findRepositoryBase";
	public static final String makeRootPrefix = "command/makeRoot";
	public static final String emailSaltPrefix = "command/emailSalt";

	public static final int okStatusCode = 200;
	public static final int notFoundStatusCode = 404;

	public static final int testPort = 8080;
	public static final String dataKey = "data";
	public static final Set<Integer> okStatusCodes = Sets.makeSet(okStatusCode);
	public static final int staleCachePeriodForTest = 500;
	public static final long clientTimeOut = 2000;

}

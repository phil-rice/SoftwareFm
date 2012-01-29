package org.softwareFm.server.constants;

public class CommonConstants {

	public final static String dataFileName = "data.json";
	public static final String DOT_GIT = ".git";
	public static final String lockFileName = "lock.lck";
	public static final String typeTag = "sling:resourceType";
	public static final String collectionType = "collection";
	
	public static final long testTimeOutMs = 500;
	
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";
	public static final String HEAD = "HEAD";
	
	public static final String findRepositoryBasePrefix = "command/findRepositoryBase";
	public static final String makeSaltPrefix = "command/makeLoginSalt";
	public static final String makeRootPrefix = "command/makeRoot";
	public static final String loginCommandPrefix = "command/login";
	public static final String signupPrefix = "command/signup";
	public static final String forgottonPasswordPrefix = "command/forgottonPassword";
	public static final String passwordResetLinkPrefix = "command/resetPassword";
	public static final String emailSaltPrefix = "command/emailSalt";
	public static final String changePasswordPrefix = "command/changePassword";
	public static final String usagePrefix = "command/Usage";
	public static final int okStatusCode = 200;
	public static final int notFoundStatusCode = 404;
	public static final String dataParameterName = "data";
	public static final String repoUrlKey = "repoUrl";
	public static final int testPort = 8080;
	public static final String dataKey = "data";


}

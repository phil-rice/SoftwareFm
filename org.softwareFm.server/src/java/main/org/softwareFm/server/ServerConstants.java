package org.softwareFm.server;

public class ServerConstants {
	public final static String dataFileName = "data.json";
	public static final String collectionType = "collection";
	public static final String typeTag = "sling:resourceType";
	public static final String cannotCreateGitUnderSecondRepository = "Cannot create git {0} under second repository";
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";
	public static final String HEAD = "HEAD";
	public static final String couldntStop = "Couldn't stop the server";
	public static final long clientTimeOut = 500;
	public static final int okStatusCode = 200;
	public static final int notFoundStatusCode = 404;
	public static final String dataParameterName = "data";

	public static final String findRepositoryBasePrefix = "command/findRepositoryBase";
	public static final String makeSaltPrefix = "command/makeLoginSalt";
	public static final String makeRootPrefix = "command/makeRoot";
	public static final String loginCommandPrefix = "command/login";
	public static final String signupPrefix = "command/signup";
	public static final String forgottonPasswordPrefix = "command/forgottonPassword";
	public static final String passwordResetLinkPrefix = "command/resetPassword";

	public static final int testPort = 8080;
	public static final String alreadyInCache = "<Already in Cache>";
	public static final String notFoundMessage = "<Not found>";
	public static final String foundMessage = "<Cloned>";
	public static final String DOT_GIT = ".git";
	public static final String madeRoot = "Made root at location {0}";
	public static final String cannotPullWhenLocalRepositoryDoesntExist = "Cannot pull {0} when local repository doesn''t exist";
	public static final String cannotPostWhenLocalRepositoryDoesntExist = "Cannot post {0} when local repository doesn''t exist";
	public static final String dataKey = "data";
	public static final String repoUrlKey = "repoUrl";
	public static final String cannotParseReplyFromGet = "Cannot parse answer from get: {0}";
	public static final long staleCacheTime = 60 * 5 * 1000;// millisecond before need to pull again
	public static final long staleAboveRepositoryCacheTime = 60 * 60 * 5 * 1000;// millisecond before need to clear above repository cache (should never need to, but I think it won't hurt...)
	public static final long staleCacheTimeForTests = 500;// hopefully enough to remove randomness without slowing tests too much
	public static final String snipperTemplateKey = "snippet.template";
	public static final String errorInGit = "Error in git. Url {0}";
	public static final String deleting = "Deleting {0}";
	public static final String cannotDelete = "Cannot delete {0}";
	public static final String cannotClearDirectory = "Cannot clear directory: {0}";
	public static final String cannotFindRepositoryFor = "Cannot find repository for {0}";
	public static final String saltKey = "salt";
	public static final String passwordHashKey = "passwordHash";
	public static final String emailKey = "email";
	public static final String emailPasswordMismatch = "Email / Password didn't match";
	public static final String cryptoKey = "crypto";
	public static final String invalidSaltMessage = "invalid salt message";
	public static final String existingEmailAddress = "Existing email address {0}";
	public static final String duplicateEmailAndPassword = "Duplicate email and password {0} and {1}";
	public static final String forgottonPasswordMessage = "You've lost your SoftwareFM password? Say it isn't so!\n\nVisit this site www.softwarefm.org/" + passwordResetLinkPrefix + "/{1} to reset it.";
	public static final String passwordResetSubject = "Software FM Password Reset";
	public static final String emailAddressNotFound = "Email address {0} not known";
	public static final String passwordResetHtml = "<html><body>Your new password is: {0}</html>";
	public static final String failedToResetPasswordHtml = "<html><body>Failed to reset. Please request a new link</body></html>";

}

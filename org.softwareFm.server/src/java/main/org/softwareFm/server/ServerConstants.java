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
	public static final String makeRootPrefix = "command/makeRoot";
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
}

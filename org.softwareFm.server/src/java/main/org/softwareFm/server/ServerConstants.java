package org.softwareFm.server;

public class ServerConstants {
	public final static String dataFileName = "data.json";
	public static final String collectionType = "collection";
	public static final String typeTag = "sling:resourceType";
	public static final String cannotCreateGitUnderSecondRepository = "Cannot create git {0} under second repository";
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String couldntStop = "Couldn't stop the server";
	public static final long clientTimeOut = 1000;
	public static final int okStatusCode = 200;
	public static final int notFoundStatusCode = 401;
	public static final String dataParameterName = "data";
	public static final String findRepositoryBasePrefix = "command/findRepositoryBase";
	public static final int testPort = 8080;
	public static final String alreadyInCache = "<Already in Cache>";
	public static final String notFoundMessage = "<Not found>";
	public static final String foundMessage = "<Cloned>";
	public static final String DOT_GIT = ".git";
}

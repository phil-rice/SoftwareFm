package org.softwareFm.crowdsource.constants;

public class GitMessages {

	public static final String alreadyUnderRepo = "Url {0} is already under repo {1}";
	public static final String notRepo = "Url {0} is not a repo";
	public static final String notUnderRepo = "Url {0} is not under a repo";
	public static final String tryingToLockUnderRepo = "Url {0} is not a valid repo url, as it is under {1}";
	public static final String cannotRelock = "RepoRl {0} already locked";
	public static final String overlappingFileLock = "Overlapping file lock";
	public static final String init = "Initial commit";
	public static final String cannotUseRepoAfterCommitOrRollback = "Cannot use after Commit or Rollback. Method {0} Args {1}";
	public static final String cannotMakeChangeToSameItemTwice = "Cannot make change to same item twice. {0} {1} {2}. Currently this is to be changed to {3}";
	public static final String commitMessageNotSet = "Commit message not set";
	public static final String commitMessageAlreadySet = "Commit message already set\nOld: {0}\nNew: {1}";
	public static final String cannotCloneRepo = "Cannot clone repo {0} because of existing repo {1}";
	public static final String doNotUnderstandRepoCommand = "Command {0} is not recognised. Url is {1}, Parameters are {2}";
	public static final String fileDigestNotPresent = "File Digest not present in {0}. {1} {2}";
	public static final String tokenNotPresent = "Token not present in {0}. {1} {2}";

}

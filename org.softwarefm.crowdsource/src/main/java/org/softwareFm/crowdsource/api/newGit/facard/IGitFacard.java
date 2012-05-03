package org.softwareFm.crowdsource.api.newGit.facard;

import java.io.File;
import java.nio.channels.FileLock;

import org.eclipse.jgit.storage.file.FileRepository;
import org.softwareFm.crowdsource.api.newGit.IAccessControlList;
import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.api.newGit.exceptions.AlreadyUnderRepoException;
import org.softwareFm.crowdsource.api.newGit.exceptions.NotRepoException;
import org.softwareFm.crowdsource.api.newGit.exceptions.NotUnderRepoException;
import org.softwareFm.crowdsource.api.newGit.exceptions.TryingToLockUnderRepoException;
import org.softwareFm.crowdsource.api.newGit.internal.GitFacard;
import org.softwareFm.crowdsource.utilities.transaction.RedoTransactionException;

/** DO NOT USE THIS NORMALLY. This is the low level accessor to git and the file system. Should only be used by tests and by GitDataPrim. */

public interface IGitFacard {

	/** Stops other people messing with this repo. It can be called on a repoRl that doesn't exist yet, but not on a rl that is under a repoRl. If a lock already exists RedoTransactionException is thrown */
	FileLock lock(String repoRl) throws RedoTransactionException, TryingToLockUnderRepoException;

	void unLock(FileLock lock);

	RepoLocation init(String repoRl) throws AlreadyUnderRepoException;

	RepoRlAndText getFile(String rl) throws NotUnderRepoException;

	String putFileReturningRepoRl(String rl, String text) throws NotUnderRepoException;

	FileRepository addAll(String repoRl) throws NotRepoException;

	RepoLocation findRepoRl(final String rl);

	void commit(FileRepository fileRepository, String commitMessage) throws NotRepoException;

	void rollback(FileRepository fileRepository) throws NotRepoException;

	public static class Utils {
		
		public static void clone(ILinkedGitFacard gitFacard, String repoRl){
			gitFacard.init(repoRl);
			gitFacard.setConfigForRemotePull(repoRl);
			gitFacard.pull(repoRl);
		}
		public static ILinkedGitFacard makeLocalGitFacard(File root, String remotePrefix, IAccessControlList acl) {
			return new GitFacard(root, remotePrefix, acl);
		}

		public static IGitFacard makeServerGitFacard(File root, IAccessControlList acl) {
			return new GitFacard(root, null, acl);
		}
	}
}

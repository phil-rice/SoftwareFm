package org.softwareFm.crowdsource.api.newGit.facard;

import java.nio.channels.FileLock;

import org.eclipse.jgit.storage.file.FileRepository;
import org.softwareFm.crowdsource.utilities.transaction.RedoTransactionException;

/** Stateless object that is low level accessor to git. Should only be used by tests and by GitDataPrim. DO NOT USE THIS NORMALLY. */

public interface IGitFacard {

	/** Stops other people messing with this repo. It can be called on a repoRl that doesn't exist yet, but not on a rl that is under a repoRl. If a lock already exists RedoTransactionException is thrown */
	FileLock lock(String repoRl) throws RedoTransactionException, TryingToLockUnderRepoException;

	void unLock(FileLock lock);

	void init(String repoRl) throws AlreadyUnderRepoException;

	String getFile(String rl) throws NotUnderRepoException;

	String putFileReturningRepoRl(String rl, String text) throws NotUnderRepoException;

	FileRepository addAll(String repoRl) throws NotRepoException;

	void commit(FileRepository fileRepository, String commitMessage) throws NotRepoException;

	void rollback(FileRepository fileRepository) throws NotRepoException;
}

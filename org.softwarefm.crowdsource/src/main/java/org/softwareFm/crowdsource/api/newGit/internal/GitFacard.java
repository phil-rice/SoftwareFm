package org.softwareFm.crowdsource.api.newGit.internal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.text.MessageFormat;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.softwareFm.crowdsource.api.newGit.facard.AlreadyUnderRepoException;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.api.newGit.facard.NotRepoException;
import org.softwareFm.crowdsource.api.newGit.facard.NotUnderRepoException;
import org.softwareFm.crowdsource.api.newGit.facard.TryingToLockUnderRepoException;
import org.softwareFm.crowdsource.constants.GitMessages;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.transaction.RedoTransactionException;

public class GitFacard implements IGitFacard {

	private final File root;
	private final FileRepositoryBuilder builder = new FileRepositoryBuilder();

	public GitFacard(File root) {
		this.root = root;
	}

	@Override
	public FileLock lock(String repoRl) throws RedoTransactionException, TryingToLockUnderRepoException {
		File repoDir = findRepoRl(repoRl);
		File lockDir = new File(root, repoRl);
		if (repoDir != null && !repoDir.equals(lockDir))
			throw new TryingToLockUnderRepoException(offset(repoDir), repoRl);
		lockDir.mkdirs();
		File lockFile = new File(lockDir, CommonConstants.lockFileName);
		try {
			lockFile.createNewFile();
			FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel();
			// Get an exclusive lock on the whole file
			try {
				FileLock lock = channel.tryLock();
				if (lock == null)
					throw new RedoTransactionException(MessageFormat.format(GitMessages.cannotRelock, repoRl));
				return lock;
			} catch (OverlappingFileLockException e) {
				channel.close();
				throw new RedoTransactionException(MessageFormat.format(GitMessages.cannotRelock, repoRl), e);
			}
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void unLock(FileLock lock) {
		Files.releaseAndClose(lock);
	}

	@Override
	public void init(String repoRl) throws AlreadyUnderRepoException {
		File existing = findRepoRl(repoRl);
		if (existing != null)
			throw new AlreadyUnderRepoException(offset(existing), repoRl);
		File repoDir = new File(root, repoRl);
		Git git = Git.init().setDirectory(repoDir).call();
		git.getRepository().close();
		FileRepository fileRepository = addAll(repoRl);
		commit(fileRepository, GitMessages.init);
	}

	@Override
	public String getFile(String rl) throws NotUnderRepoException {
		findMustExistRepoRl(rl);
		return Files.getText(new File(root, rl));
	}

	@Override
	public String putFileReturningRepoRl(String rl, String text) throws NotUnderRepoException {
		File dir = findRepoRl(rl);
		if (dir == null)
			throw new NotUnderRepoException(rl);
		File file = new File(root, rl);
		Files.makeDirectoryForFile(file);
		Files.setText(file, text);
		return offset(dir);
	}

	@Override
	public FileRepository addAll(String repoRl) throws NotRepoException {
		try {
			File repoDir = findMustExistRepoRl(repoRl);
			FileRepository fileRepository = makeFileRepository(repoDir);
			new AddCommand(fileRepository).addFilepattern(".").call();
			return fileRepository;
		} catch (NoFilepatternException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void commit(FileRepository fileRepository, String commitMessage) throws NotRepoException {
		try {
			Git git = new Git(fileRepository);
			CommitCommand commit = git.commit();
			commit.setAll(true).setMessage(commitMessage);
			commit.call();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		} finally {
			fileRepository.close();
		}
	}

	@Override
	public void rollback(FileRepository fileRepository) throws NotRepoException {
		try {
			Git git = new Git(fileRepository);
			ResetCommand reset = git.reset();
			reset.setMode(ResetType.HARD);
			reset.call();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		} finally {
			fileRepository.close();
		}

	}

	private File findMustExistRepoRl(String rl) {
		File result = findRepoRl(rl);
		if (result == null)
			throw new NotUnderRepoException(rl);
		return result;
	}

	private File findRepoRl(final String rl) {
		final File dir = new File(root, rl);
		for (File file : Files.listParentsUntil(root, dir))
			if (new File(file, CommonConstants.DOT_GIT).exists())// found it
				return file;
		return null;
	}

	private String offset(File file) {
		return Files.offset(root, file);
	}

	private FileRepository makeFileRepository(final File repoDir) {
		try {
			FileRepository repository = builder.//
					setGitDir(new File(repoDir, CommonConstants.DOT_GIT))//
					.readEnvironment() // scan environment GIT_* variables
					.build(); // scan up the file system tree
			return repository;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}

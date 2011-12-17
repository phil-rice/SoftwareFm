package org.softwareFm.server.internal;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;

public class GitFacard implements IGitFacard {

	@Override
	public String getConfig(File root, String url, String section, String subsection, String name) {
		FileRepository fileRepository = makeFileRepository(root, url);
		FileBasedConfig gitConfig = fileRepository.getConfig();
		String result = gitConfig.getString(section, subsection, name);
		return result;
	}

	@Override
	public void createRepository(File root, String url) {
		try {
			File file = findRepositoryUrl(root, url);
			if (file != null)
				throw new IllegalArgumentException(MessageFormat.format(ServerConstants.cannotCreateGitUnderSecondRepository, url));
			File fullRoot = new File(root, url);
			Git.init().setDirectory(fullRoot).call();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	public String getBranch(File root, String url) {
		try {
			FileRepository fileRepository = makeFileRepository(root, url);
			return fileRepository.getBranch();
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void addAllAndCommit(File root, String url, String message) {
		try {
			FileRepository fileRepository = makeFileRepository(root, url);
			Git git = new Git(fileRepository);
			new AddCommand(fileRepository).addFilepattern(".").call();
			CommitCommand commit = git.commit();
			commit.setAll(true).setMessage(message);
			commit.call();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	public void pull(File root, String url) {
		try {
			FileRepository fileRepository = makeFileRepository(root, url);
			PullCommand pull = new Git(fileRepository).pull();
			pull.call();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	};

	@Override
	public File findRepositoryUrl(File root, String url) {
		final File dir = new File(root, url);
		for (File file : Files.listParentsUntil(root, dir))
			if (new File(file, ServerConstants.DOT_GIT).exists())// found it
				return file;
		return null;
	}

	@Override
	public FileRepository makeFileRepository(File root, String url) {
		try {
			File dir = findRepositoryUrl(root, url);
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			return builder.setGitDir(new File(dir, ServerConstants.DOT_GIT)).readEnvironment() // scan environment GIT_* variables
					.build(); // scan up the file system tree
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void clone(String fromUri, File targetRoot, String targetUri) {
		File targetDirectory = new File(targetRoot, targetUri);
		Git call = Git.cloneRepository().//
				setDirectory(targetDirectory).//
				setURI(fromUri).//
				// setBranch("master").//
				call();
		call.getRepository().close();
		FileRepository fileRepository = makeFileRepository(targetRoot, targetUri);
		FileBasedConfig config = fileRepository.getConfig();
		config.setString("branch", "master", "remote", "origin");
		config.setString("branch", "master", "merge", "refs/heads/master");
		try {
			config.save();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
}

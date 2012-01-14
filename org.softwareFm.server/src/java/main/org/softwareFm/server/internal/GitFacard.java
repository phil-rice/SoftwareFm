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
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.strings.Urls;

public class GitFacard implements IGitFacard {

	@Override
	public String getConfig(File root, String url, final String section, final String subsection, final String name) {
		return useFileRepository(root, url, new IFunction1<FileRepository, String>() {
			@Override
			public String apply(FileRepository from) throws Exception {
				FileBasedConfig gitConfig = from.getConfig();
				String result = gitConfig.getString(section, subsection, name);
				return result;
			}
		});
	}

	@Override
	public void createRepository(File root, String url) {
		try {
			File fullRoot = new File(root, url);
			File file = findRepositoryUrl(root, url);
			if (fullRoot.equals(file))
				return;
			if (file != null)
				throw new IllegalArgumentException(MessageFormat.format(ServerConstants.cannotCreateGitUnderSecondRepository, url));
			Git git = Git.init().setDirectory(fullRoot).call();
			git.getRepository().close();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	public void delete(final File root, final String url) {
		useFileRepository(root, url, new IFunction1<FileRepository, Void>() {
			@Override
			public Void apply(FileRepository fileRepository) throws Exception {
				Git git = new Git(fileRepository);
				File file = new File(root, Urls.compose(url, ServerConstants.dataFileName));
				if (!file.delete())
					throw new RuntimeException(MessageFormat.format(ServerConstants.cannotDelete, file));
				new AddCommand(fileRepository).addFilepattern(".").call();
				CommitCommand commit = git.commit();
				commit.setAll(true).setMessage(MessageFormat.format(ServerConstants.deleting, url));
				commit.call();
				return null;
			}
		});

	}

	@Override
	public String getBranch(File root, String url) {
		return useFileRepository(root, url, new IFunction1<FileRepository, String>() {
			@Override
			public String apply(FileRepository fileRepository) throws Exception {
				return fileRepository.getBranch();
			}
		});
	}

	@Override
	public void addAllAndCommit(File root, String url, final String message) {
		useFileRepository(root, url, new IFunction1<FileRepository, Void>() {
			@Override
			public Void apply(FileRepository fileRepository) throws Exception {
				Git git = new Git(fileRepository);
				new AddCommand(fileRepository).addFilepattern(".").call();
				CommitCommand commit = git.commit();
				commit.setAll(true).setMessage(message);
				commit.call();
				return null;
			}
		});
	}

	@Override
	public void pull(File root, String url) {
		useFileRepository(root, url, new IFunction1<FileRepository, Void>() {
			@Override
			public Void apply(FileRepository fileRepository) throws Exception {
				PullCommand pull = new Git(fileRepository).pull();
				pull.call();
				return null;
			}
		});
	}

	@Override
	public File findRepositoryUrl(File root, String url) {
		final File dir = new File(root, url);
		for (File file : Files.listParentsUntil(root, dir))
			if (new File(file, ServerConstants.DOT_GIT).exists())// found it
				return file;
		return null;
	}

	@Override
	public void gc(File root, String url) {
		File file = new File(root, url);
		try {
			Runtime.getRuntime().exec("git", new String[] { "gc" }, file);
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	public <T> T useFileRepository(File root, String url, IFunction1<FileRepository, T> callback) {
		try {
			File dir = findRepositoryUrl(root, url);
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			FileRepository repository = builder.setGitDir(new File(dir, ServerConstants.DOT_GIT)).readEnvironment() // scan environment GIT_* variables
					.build(); // scan up the file system tree
			try {
				T result = callback.apply(repository);
				return result;
			} finally {
				repository.close();
			}
		} catch (Exception e) {
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
		useFileRepository(targetRoot, targetUri, new IFunction1<FileRepository, Void>() {
			@Override
			public Void apply(FileRepository fileRepository) throws Exception {
				FileBasedConfig config = fileRepository.getConfig();
				config.setString("branch", "master", "remote", "origin");
				config.setString("branch", "master", "merge", "refs/heads/master");
				try {
					config.save();
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
				return null;
			}
		});
	}
}

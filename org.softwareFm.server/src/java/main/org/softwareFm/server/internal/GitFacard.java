package org.softwareFm.server.internal;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;

public class GitFacard implements IGitFacard {

	@Override
	public void pull(File root, String fromUri) {
		try {
			FileRepository fileRepository = makeFileRepository(root, fromUri);
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
			if (new File(file, ServerConstants.gitDirectory).exists())// found it
				return file;
		return null;
	}

	@Override
	public FileRepository makeFileRepository(File root, String url) {
		try {
			File dir = findRepositoryUrl(root, url);
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			return builder.setGitDir(new File(dir, ServerConstants.gitDirectory)).readEnvironment() // scan environment GIT_* variables
					.build(); // scan up the file system tree
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void clone(String fromUri, File targetDirectory) {
		Git call = Git.cloneRepository().//
				setDirectory(targetDirectory).//
				setURI(fromUri).//
				call();
		call.getRepository().close();
	}
}

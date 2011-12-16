package org.softwareFm.server.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;

/**
 * git server maintains none-bare repositories. Changes are made to file system, then git commit is called. There is a file lock at the repository level for the duration of the put. Note that this is single lock per repository, not single lock per server
 * 
 * @author Phil
 * 
 */
public class GitServer extends LocalGitClient implements IGitServer {

	private final IGitFacard gitFacard;

	public GitServer(IGitFacard gitFacard, File root) {
		super(root);
		this.gitFacard = gitFacard;
	}

	@Override
	public void createRepository(final String url) {
		final File dir = new File(root, url);
		Files.doOperationInLock(dir, "lock", new ICallback<File>() {
			@Override
			public void process(File lockFile) throws Exception {
				File existing = findRepositoryUrl(url);
				if (existing != null) {
					throw new IllegalStateException(MessageFormat.format(ServerConstants.cannotCreateGitUnderSecondRepository, url));
				}
				File gitDir = new File(dir, ServerConstants.gitDirectory);
				dir.mkdirs();
				FileRepository fileRepository = new FileRepositoryBuilder().setGitDir(gitDir).readEnvironment().build();
				fileRepository.create();// we don't want a bare repository
			}
		});
	}

	@Override
	public File findRepositoryUrl(String url) {
		checkCleanUrl(url);
		final File dir = new File(root, url);
		for (File file : Files.listParentsUntil(root, dir))
			if (new File(file, ServerConstants.gitDirectory).exists())// found it
				return file;
		return null;
	}

	private void checkCleanUrl(String url) {
		// TODO check that the urls are just identifier/identifier...
	}

	@Override
	public void post(String url, Map<String, Object> map) {
		super.post(url, map);
		addAllAndCommit(url, "message");
	}

	private void addAllAndCommit(String url, String message) {
		try {
			FileRepository fileRepository = gitFacard.makeFileRepository(root, url);
			Git git = new Git(fileRepository);
			new AddCommand(fileRepository).addFilepattern(".").call();
			CommitCommand commit = git.commit();
			commit.setAll(true).setMessage(message);
			commit.call();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

}

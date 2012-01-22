package org.softwareFm.server.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.url.Urls;

public class GitServer extends LocalGitClient implements IGitServer {

	private final IGitFacard gitFacard;
	private final String remoteUriPrefix;

	public GitServer(IGitFacard gitFacard, File root, String remoteUriPrefix) {
		super(root);
		this.gitFacard = gitFacard;
		this.remoteUriPrefix = remoteUriPrefix;
	}

	@Override
	public void createRepository(final String url) {
		gitFacard.createRepository(root, url);
	}

	@Override
	public void pull(String url) {
		try {
			File existing = findRepositoryUrl(url);
			if (existing == null)
				throw new IllegalArgumentException(MessageFormat.format(ServerConstants.cannotPullWhenLocalRepositoryDoesntExist, url));
			gitFacard.pull(root, url);
		} catch (Exception e) {
			throw new RuntimeException(MessageFormat.format(ServerConstants.errorInGit, url), e);
		}
	}

	@Override
	public void delete(IFileDescription fileDescription) {
		super.delete(fileDescription);
		gitFacard.addAllAndCommit(root, fileDescription, "message");
	}

	@Override
	public void clone(String url) {
		gitFacard.clone(Urls.compose(remoteUriPrefix, url), root, url);
	}

	@Override
	public File findRepositoryUrl(String url) {
		final File dir = new File(root, url);
		for (File file : Files.listParentsUntil(root, dir))
			if (new File(file, ServerConstants.DOT_GIT).exists())// found it
				return file;
		return null;
	}

	@Override
	public void post(IFileDescription fileDescription, Map<String, Object> map) {
		super.post(fileDescription, map);
		addAllAndCommit(fileDescription, "message");
	}

	private void addAllAndCommit(IFileDescription fileDescription, String message) {
		try {
			gitFacard.addAllAndCommit(root, fileDescription, message);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	public String toString() {
		return "GitServer [gitFacard=" + gitFacard + ", remoteUriPrefix=" + remoteUriPrefix + ", Root=" + root +"]";
	}

}

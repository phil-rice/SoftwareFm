package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;

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
		gitFacard.pull(root, url);
	}

	@Override
	public void clone(String url) {
		gitFacard.clone(remoteUriPrefix + "/" + url, root, url);
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
	public void post(String url, Map<String, Object> map) {
		super.post(url, map);
		addAllAndCommit(url, "message");
	}

	private void addAllAndCommit(String url, String message) {
		try {
			gitFacard.addAllAndCommit(root, url, message);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

}

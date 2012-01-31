package org.softwareFm.server.internal;

import java.io.File;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IRepoFinder;
import org.softwareFm.server.RepoDetails;

public class RepoFinderForTests implements IRepoFinder {
	private final File root;

	public RepoFinderForTests(File root) {
		this.root = root;
	}

	@Override
	public RepoDetails findRepoUrl(String url) {
		String repositoryUrl = IFileDescription.Utils.findRepositoryUrl(root, url);
		if (repositoryUrl == null)
			throw new UnsupportedOperationException();
		return RepoDetails.repositoryUrl(repositoryUrl);
	}

	@Override
	public void clearCaches() {
	}
}
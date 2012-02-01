package org.softwareFm.server.internal;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.IRepoFinder;
import org.softwareFm.server.RepoDetails;

public class RepoFinderForTests implements IRepoFinder {
	private final IGitOperations remoteOperations;

	public RepoFinderForTests(IGitOperations remoteOperations) {
		this.remoteOperations = remoteOperations;
	}

	@Override
	public RepoDetails findRepoUrl(String url) {
		String repositoryUrl = IFileDescription.Utils.findRepositoryUrl(remoteOperations.getRoot(), url);
		if (repositoryUrl == null)
			return RepoDetails.aboveRepo(remoteOperations.getFileAndDescendants(IFileDescription.Utils.plain(url)));
		else
			return RepoDetails.repositoryUrl(repositoryUrl);
	}

	@Override
	public void clearCaches() {
	}

	@Override
	public void clearCache(String url) {

	}
}
package org.softwareFm.common.internal;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IRepoFinder;
import org.softwareFm.common.RepoDetails;

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
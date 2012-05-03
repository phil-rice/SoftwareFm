package org.softwareFm.crowdsource.api.newGit.internal;

import org.softwareFm.crowdsource.api.newGit.IRepoLocator;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;

public class SimpleRepoLocator implements IRepoLocator {
	private final IGitFacard gitFacard;

	public SimpleRepoLocator(IGitFacard gitFacard) {
		super();
		this.gitFacard = gitFacard;
	}

	@Override
	public RepoLocation findRepository(ISingleSource source) {
		String fullRl = source.fullRl();
		RepoLocation repoLocation = gitFacard.findRepoRl(fullRl);
		return repoLocation;
	}
}

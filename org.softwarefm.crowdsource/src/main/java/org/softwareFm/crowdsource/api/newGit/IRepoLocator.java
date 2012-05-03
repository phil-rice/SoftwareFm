package org.softwareFm.crowdsource.api.newGit;


public interface IRepoLocator {

	/** Given the source, where is the repository that stores the data about the source */
	RepoLocation findRepository(ISingleSource source);

}

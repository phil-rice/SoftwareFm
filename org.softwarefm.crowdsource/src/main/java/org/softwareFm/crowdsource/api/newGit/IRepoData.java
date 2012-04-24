package org.softwareFm.crowdsource.api.newGit;

import java.util.List;

import org.softwareFm.crowdsource.api.newGit.internal.Sources;

/**
 * This is a mixin interface, providing helper methods for easier access to IGitDataPrim. Each transaction will have its own unique IGitData<br />
 * 
 * These methods must all be called in a transaction, or they will fail. None of the changes will take place until the transaction is committed.<br />
 * 
 * Other transactions are locked out from messing with the repositories accessed by these methods
 */
public interface IRepoData extends IRepoPrim, IRepoReader {

	/** The current value is merged with the existing map at that index */
	void setProperty(ISingleSource source, int index, String name, Object value);

	RepoLocation findRepository(ISingleSource singleSource);

	List<RepoLocation> findRepositories(Sources sources);

}

package org.softwareFm.crowdsource.api.newGit.internal;

import java.nio.channels.FileLock;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.newGit.IRepoLocator;
import org.softwareFm.crowdsource.api.newGit.IRepoReaderImplementor;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.api.newGit.facard.ILinkedGitFacard;
import org.softwareFm.crowdsource.utilities.collections.ITransactionalMutableSimpleSet;

public class LinkedRepoReader implements IRepoReaderImplementor {

	final IRepoReaderImplementor delegate;
	final ILinkedGitFacard linkedGitFacard;
	final IRepoLocator repoLocator;
	final ITransactionalMutableSimpleSet<String> hasPulled;

	public LinkedRepoReader(IRepoReaderImplementor delegate, ILinkedGitFacard linkedGitFacard, IRepoLocator repoLocator, ITransactionalMutableSimpleSet<String> hasPulled) {
		super();
		this.delegate = delegate;
		this.linkedGitFacard = linkedGitFacard;
		this.repoLocator = repoLocator;
		this.hasPulled = hasPulled;
	}

	@Override
	public List<String> readRaw(ISingleSource singleSource) {
		cloneOrPullIfNeeded(singleSource);
		return delegate.readRaw(singleSource);
	}

	@Override
	public Map<String, Object> readRow(ISingleSource singleSource, int row) {
		cloneOrPullIfNeeded(singleSource);
		return delegate.readRow(singleSource, row);
	}

	@Override
	public void commit() {
		hasPulled.commit();
		delegate.commit();
	}

	@Override
	public void rollback() {
		try {
			delegate.rollback();
		} finally {
			hasPulled.rollback();
		}
	}

	@Override
	public Map<ISingleSource, List<String>> rawCache() {
		return delegate.rawCache();
	}

	@Override
	public Map<String, FileLock> locks() {
		return delegate.locks();
	}

	private void cloneOrPullIfNeeded(ISingleSource singleSource) {
		RepoLocation localLocation = linkedGitFacard.findRepoRl(singleSource.fullRl());
		if (localLocation == null) {
			RepoLocation remoteLocation = repoLocator.findRepository(singleSource);
			IGitFacard.Utils.clone(linkedGitFacard, remoteLocation.rl);
			localLocation = linkedGitFacard.findRepoRl(singleSource.fullRl());
		} else {
			if (hasPulled.contains(localLocation.rl))
				return;
			linkedGitFacard.pull(localLocation.rl);
		}
		hasPulled.add(localLocation.rl);
	}

}

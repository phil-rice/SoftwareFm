package org.softwareFm.crowdsource.api.newGit;

import org.softwareFm.crowdsource.api.IFactory;
import org.softwareFm.crowdsource.api.newGit.facard.ILinkedGitFacard;
import org.softwareFm.crowdsource.api.newGit.internal.LinkedRepoReader;
import org.softwareFm.crowdsource.api.newGit.internal.RepoData;
import org.softwareFm.crowdsource.api.newGit.internal.SimpleRepoLocator;
import org.softwareFm.crowdsource.api.newGit.internal.SimpleRepoReader;
import org.softwareFm.crowdsource.utilities.collections.ITransactionalMutableSimpleSet;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public interface IRepoDataFactory extends IFactory<IRepoData> {

	public static class Utils {
		public static IRepoDataFactory simpleFactory(final ILinkedGitFacard gitFacard) {
			return new IRepoDataFactory() {
				@Override
				public IRepoData build() {
					return new RepoData(gitFacard, new IFunction1<ILinkedGitFacard, IRepoReaderImplementor>() {
						@Override
						public IRepoReaderImplementor apply(ILinkedGitFacard from) throws Exception {
							return new SimpleRepoReader(from);
						}
					}, new SimpleRepoLocator(gitFacard));
				}
			};
		}

		public static IRepoDataFactory localFactory(final ILinkedGitFacard localGitFacard, final IRepoLocator repoLocator, final ITransactionalMutableSimpleSet<String> hasPulled) {
			return new IRepoDataFactory() {
				
				@Override
				public IRepoData build() {
					return new RepoData(localGitFacard, new IFunction1<ILinkedGitFacard, IRepoReaderImplementor>() {
						
						@Override
						public IRepoReaderImplementor apply(ILinkedGitFacard from) throws Exception {
							SimpleRepoReader delegate = new SimpleRepoReader(localGitFacard);
							return new LinkedRepoReader(delegate, from, repoLocator, hasPulled);
						}
					}, repoLocator);
				}
			};
		}
	}
}

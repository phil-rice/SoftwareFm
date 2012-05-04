package org.softwareFm.crowdsource.api.newGit;

import java.util.Set;

import org.softwareFm.crowdsource.api.IFactory;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.api.newGit.facard.ILinkedGitFacard;
import org.softwareFm.crowdsource.api.newGit.internal.LinkedRepoReader;
import org.softwareFm.crowdsource.api.newGit.internal.RepoData;
import org.softwareFm.crowdsource.api.newGit.internal.SimpleRepoLocator;
import org.softwareFm.crowdsource.api.newGit.internal.SimpleRepoReader;
import org.softwareFm.crowdsource.utilities.collections.Sets;

public interface IRepoDataFactory extends IFactory<IRepoData> {

	public static class Utils {
		public static IRepoDataFactory simpleFactory(final IGitFacard gitFacard) {
			return new IRepoDataFactory() {
				@Override
				public IRepoData build() {
					SimpleRepoReader repoReader = new SimpleRepoReader(gitFacard);
					SimpleRepoLocator repoLocator = new SimpleRepoLocator(gitFacard);
					return new RepoData(gitFacard, repoReader, repoLocator);
				}
			};
		}

		public static IRepoDataFactory localFactory(final ILinkedGitFacard localGitFacard, final IRepoLocator repoLocator, final Set<String> hasPulled) {
			return new IRepoDataFactory() {
				@Override
				public IRepoData build() {
					SimpleRepoReader delegate = new SimpleRepoReader(localGitFacard);
					LinkedRepoReader repoReader = new LinkedRepoReader(delegate, localGitFacard, repoLocator, Sets.<String> asTransactionalSet(hasPulled));
					return new RepoData(localGitFacard, repoReader, repoLocator);
				}
			};
		}
	}
}

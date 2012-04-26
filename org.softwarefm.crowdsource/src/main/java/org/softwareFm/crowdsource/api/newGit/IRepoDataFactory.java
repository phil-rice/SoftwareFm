package org.softwareFm.crowdsource.api.newGit;

import org.softwareFm.crowdsource.api.IFactory;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.api.newGit.internal.RepoData;

public interface IRepoDataFactory extends IFactory<IRepoData> {

	public static class Utils {
		public static IRepoDataFactory factory(final IGitFacard gitFacard) {
			return new IRepoDataFactory() {
				@Override
				public IRepoData build() {
					return new RepoData(gitFacard);
				}
			};
		}
	}
}

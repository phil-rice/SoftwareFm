package org.softwareFm.crowdsource.api.newGit;

import org.softwareFm.crowdsource.api.newGit.internal.RepoData;

public interface IRepoFactory {
	IRepoData make(String commitComment);

	public static class Utils {
		public static IRepoFactory makeFactory(final IRepoPrim delegate) {
			return new IRepoFactory() {

				@Override
				public IRepoData make(String commitComment) {
					return new RepoData(delegate, commitComment);
				}
			};
		}
	}
}

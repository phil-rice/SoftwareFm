package org.softwareFm.common;

import org.softwareFm.common.internal.RepoFinderForTests;
import org.softwareFm.common.maps.IHasUrlCache;

public interface IRepoFinder extends IHasUrlCache {

	RepoDetails findRepoUrl(String url);

	abstract public static class Utils {
		public static IRepoFinder forTests(IGitOperations remoteOperations) {
			return new RepoFinderForTests(remoteOperations);
		}
	}
}

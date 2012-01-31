package org.softwareFm.server;

import org.softwareFm.server.internal.RepoFinderForTests;
import org.softwareFm.utilities.maps.IHasUrlCache;

public interface IRepoFinder extends IHasUrlCache{

	RepoDetails findRepoUrl(String url);
	
	public static class Utils{
		public static IRepoFinder forTests(IGitOperations remoteOperations){
			return new RepoFinderForTests(remoteOperations);
		}
	}
}

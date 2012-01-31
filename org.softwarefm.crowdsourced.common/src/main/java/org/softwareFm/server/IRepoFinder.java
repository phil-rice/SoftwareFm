package org.softwareFm.server;

import java.io.File;

import org.softwareFm.server.internal.RepoFinderForTests;
import org.softwareFm.utilities.maps.IHasCache;

public interface IRepoFinder extends IHasCache{

	RepoDetails findRepoUrl(String url);
	
	public static class Utils{
		public static IRepoFinder forTests(File remoteRoot){
			return new RepoFinderForTests(remoteRoot);
		}
	}
}

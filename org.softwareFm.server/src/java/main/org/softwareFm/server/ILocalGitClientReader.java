package org.softwareFm.server;

import java.io.File;

public interface ILocalGitClientReader {

	/** The root file that the urls are relative to */
	File getRoot();

	/** Returns the local file, and child files (in sub directories) encoded into the map */
	GetResult localGet(IFileDescription fileDescription);

	/** Returns just the local file */
	GetResult getFile(IFileDescription fileDescription);

	public static class Utils {
		public static ILocalGitClientReader noReader() {
			return new ILocalGitClientReader() {

				@Override
				public File getRoot() {
					throw new UnsupportedOperationException();
				}

				@Override
				public GetResult localGet(IFileDescription fileDescription) {
					throw new UnsupportedOperationException();
				}

				@Override
				public GetResult getFile(IFileDescription fileDescription) {
					throw new UnsupportedOperationException();
				}

			};
		}
	}
}

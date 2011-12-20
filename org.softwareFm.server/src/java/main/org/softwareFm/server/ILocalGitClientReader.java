package org.softwareFm.server;

import java.io.File;

public interface ILocalGitClientReader {

	File getRoot();

	GetResult localGet(String url);
	
	GetResult getFile(String url);


	public static class Utils {
		public static ILocalGitClientReader noReader() {
			return new ILocalGitClientReader() {

				@Override
				public GetResult localGet(String url) {
					throw new UnsupportedOperationException();
				}

				@Override
				public File getRoot() {
					throw new UnsupportedOperationException();
				}

				@Override
				public GetResult getFile(String url) {
					throw new UnsupportedOperationException();
				}

			};
		}
	}
}

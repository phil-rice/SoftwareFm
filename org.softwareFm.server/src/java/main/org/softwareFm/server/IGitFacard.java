package org.softwareFm.server;

import java.io.File;

import org.softwareFm.server.internal.GitFacard;

public interface IGitFacard {
	void clone(String fromUri, File targetRoot, String targetUri);

	void createRepository(File root, String url);

	void pull(File root, String url);

	void delete(File root, String url);

	void gc(File root, String url);

	void addAllAndCommit(File root, IFileDescription fileDescription, String message);

	String getConfig(File root, String url, String section, String subsection, String name);

	String getBranch(File root, String url);

	public static class Utils {
		public static IGitFacard makeFacard() {
			return new GitFacard();
		}
	}

}
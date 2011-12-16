package org.softwareFm.server;

import java.io.File;

import org.eclipse.jgit.storage.file.FileRepository;
import org.softwareFm.server.internal.GitFacard;

public interface IGitFacard {
	File findRepositoryUrl(File root, String url);

	FileRepository makeFileRepository(File root, String url);

	void clone(String fromUri, File targetDirectory);
	
	void pull(File root,String fromUri);
	
	public static class Utils{
		public static IGitFacard makeFacard(){
			return new GitFacard();
		}
	}
}
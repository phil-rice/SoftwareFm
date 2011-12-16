package org.softwareFm.server.internal;

import java.io.File;

import org.eclipse.jgit.storage.file.FileRepository;
import org.softwareFm.server.IGitFacard;

public class GitFacardMock implements IGitFacard {

	@Override
	public File findRepositoryUrl(File root, String url) {
		return null;
	}

	@Override
	public FileRepository makeFileRepository(File root, String url) {
		return null;
	}

	@Override
	public void clone(String fromUri, File targetDirectory) {
		
	}
}

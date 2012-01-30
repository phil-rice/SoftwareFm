package org.softwareFm.server.internal;

import java.io.File;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.utilities.functions.IFunction1;

public class FindRepositoryForTests implements IFunction1<String, String> {
	private final File root;

	public FindRepositoryForTests(File root) {
		this.root = root;
	}

	@Override
	public String apply(String from) throws Exception {
		String repositoryUrl = IFileDescription.Utils.findRepositoryUrl(root, from);
		return repositoryUrl;
	}
}
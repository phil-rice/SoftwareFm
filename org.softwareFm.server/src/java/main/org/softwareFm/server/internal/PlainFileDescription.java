package org.softwareFm.server.internal;

import java.io.File;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;

public class PlainFileDescription implements IFileDescription {

	private final String url;

	public PlainFileDescription(String url) {
		this.url = url;
	}

	@Override
	public File getDirectory(File root) {
		return new File(root, url);
	}
	@Override
	public File findRepositoryUrl(File root) {
		final File dir = new File(root, url);
		for (File file : Files.listParentsUntil(root, dir))
			if (new File(file, ServerConstants.DOT_GIT).exists())// found it
				return file;
		return null;
	}

}

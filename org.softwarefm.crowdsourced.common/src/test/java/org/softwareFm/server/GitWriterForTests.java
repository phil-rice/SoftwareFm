package org.softwareFm.server;

import java.io.File;
import java.util.Map;

public class GitWriterForTests implements IGitWriter {
	private final IGitOperations remoteOperations;
	

	public GitWriterForTests(IGitOperations remoteOperations) {
		super();
		this.remoteOperations = remoteOperations;
	}

	@Override
	public void put(IFileDescription fileDescription, Map<String, Object> data) {
		remoteOperations.put(fileDescription, data);
	}

	@Override
	public void init(String url) {
		remoteOperations.init(url);
	}

	@Override
	public void delete(IFileDescription fileDescription) {
		File file = new File(remoteOperations.getRoot(), fileDescription.url());
		file.delete();
		IFileDescription.Utils.addAllAndCommit(remoteOperations, fileDescription, "delete: " + fileDescription);
	}
}
package org.softwareFm.common.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IGitWriter;

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
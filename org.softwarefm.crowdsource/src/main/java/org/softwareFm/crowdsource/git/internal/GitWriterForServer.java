package org.softwareFm.crowdsource.git.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.git.IGitWriter;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public class GitWriterForServer implements IGitWriter {

	private final IGitOperations gitOperations;

	public GitWriterForServer(IGitOperations gitOperations) {
		this.gitOperations = gitOperations;
	}

	@Override
	public File getRoot() {
		return gitOperations.getRoot();
	}

	@Override
	public String getFileAsString(IFileDescription fileDescription) {
		return gitOperations.getFileAsString(fileDescription);
	}

	@Override
	public Map<String, Object> getFile(IFileDescription fileDescription) {
		return gitOperations.getFile(fileDescription);
	}

	@Override
	public Iterable<Map<String, Object>> getFileAsListOfMaps(IFileDescription fileDescription) {
		return gitOperations.getFileAsListOfMaps(fileDescription);
	}

	@Override
	public int countOfFileAsListsOfMap(IFileDescription fileDescription) {
		return gitOperations.countOfFileAsListsOfMap(fileDescription);
	}

	@Override
	public Map<String, Object> getFileAndDescendants(IFileDescription fileDescription) {
		return gitOperations.getFile(fileDescription);
	}

	@Override
	public void clearCaches() {
		gitOperations.clearCaches();
	}

	@Override
	public void init(String url, String commitMessage) {
		gitOperations.init(url);
		gitOperations.addAllAndCommit(url, commitMessage);
	}

	@Override
	public void put(IFileDescription fileDescription, Map<String, Object> data, String commitMessage) {
		gitOperations.put(fileDescription, data);
		IFileDescription.Utils.addAllAndCommit(gitOperations, fileDescription, commitMessage);
	}

	@Override
	public void delete(IFileDescription fileDescription, String commitMessage) {
		if (fileDescription.getFile(getRoot()).exists()) {
			gitOperations.delete(fileDescription);
			IFileDescription.Utils.addAllAndCommit(gitOperations, fileDescription, commitMessage);
		}
	}

	@Override
	public void append(IFileDescription fileDescription, Map<String, Object> data, String commitMessage) {
		gitOperations.append(fileDescription, data);

	}

	@Override
	public int removeLine(IFileDescription fileDescription, IFunction1<Map<String, Object>, Boolean> acceptor, String commitMessage) {
		return gitOperations.removeLine(fileDescription, acceptor, commitMessage);
	}

}

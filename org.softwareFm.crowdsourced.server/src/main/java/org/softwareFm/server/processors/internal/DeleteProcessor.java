package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.collections.Files;

public class DeleteProcessor implements IProcessCall {

	private final IGitOperations gitOperations;

	public DeleteProcessor(IGitOperations gitOperations) {
		this.gitOperations = gitOperations;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(CommonConstants.DELETE)) {
			String uri = requestLine.getUri();
			IFileDescription fileDescription = IFileDescription.Utils.fromRequest(requestLine, parameters);
			File root = gitOperations.getRoot();
			File file = fileDescription.getFile(root);
			if (file.exists()) {
				file.delete();
				File repositoryFile = fileDescription.findRepositoryUrl(root);
				String repositoryUrl = Files.offset(root, repositoryFile);
				gitOperations.addAllAndCommit(repositoryUrl, "delete: " + uri);
			}
			return IProcessResult.Utils.doNothing();
		}
		return null;
	}

}

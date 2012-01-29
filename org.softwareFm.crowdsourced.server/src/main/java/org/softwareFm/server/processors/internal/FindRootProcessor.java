package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.collections.Files;

public class FindRootProcessor extends AbstractCommandProcessor {

	public FindRootProcessor(IGitOperations gitOperations) {
		super(gitOperations, CommonConstants.GET, CommonConstants.findRepositoryBasePrefix);
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		IFileDescription fileDescription = IFileDescription.Utils.plain(actualUrl);
		File root = gitOperations.getRoot();
		File repositoryFile = fileDescription.findRepositoryUrl(root);
		String result = Files.offset(root, repositoryFile);
		return IProcessResult.Utils.processString(result);
	}

}

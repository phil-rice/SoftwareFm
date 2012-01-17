package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.collections.Files;

public class FindRootProcessor extends AbstractCommandProcessor {

	public FindRootProcessor(IGitServer server) {
		super(server, ServerConstants.GET, ServerConstants.findRepositoryBasePrefix);
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		File repositoryLocation = server.findRepositoryUrl(actualUrl);
		String result = Files.offset(server.getRoot(), repositoryLocation);
		return IProcessResult.Utils.processString(result);
	}

}

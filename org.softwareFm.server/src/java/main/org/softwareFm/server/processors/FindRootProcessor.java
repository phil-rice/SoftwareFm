package org.softwareFm.server.processors;

import java.io.File;

import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;

public class FindRootProcessor extends AbstractCommandProcessor {

	public FindRootProcessor(IGitServer server) {
		super(server, ServerConstants.GET, ServerConstants.findRepositoryBasePrefix);
	}

	@Override
	protected String execute(String actualUrl) {
		File repositoryLocation = server.findRepositoryUrl(actualUrl);
		String result = Files.offset(server.getRoot(), repositoryLocation);
		return result;
	}

}

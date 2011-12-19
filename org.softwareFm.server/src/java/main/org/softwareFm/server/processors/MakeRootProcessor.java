package org.softwareFm.server.processors;

import java.io.File;
import java.text.MessageFormat;

import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;

public class MakeRootProcessor extends AbstractCommandProcessor {

	public MakeRootProcessor(IGitServer server) {
		super(server, ServerConstants.POST, ServerConstants.makeRootPrefix);
	}

	@Override
	protected String execute(String actualUrl) {
		File existing = server.findRepositoryUrl(actualUrl);
		if (existing == null)
			server.createRepository(actualUrl);
		return MessageFormat.format(ServerConstants.madeRoot, actualUrl);
	}
}

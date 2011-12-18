package org.softwareFm.server.processors;

import java.text.MessageFormat;

import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;

public class MakeRootProcessor extends AbstractCommandProcessor {

	public MakeRootProcessor(IGitServer server) {
		super(server, ServerConstants.POST, ServerConstants.makeRootPrefix);
	}

	@Override
	protected String execute(String actualUrl) {
		server.createRepository(actualUrl);
		return MessageFormat.format(ServerConstants.madeRoot, actualUrl);
	}
}

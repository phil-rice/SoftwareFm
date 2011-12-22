package org.softwareFm.server.processors.internal;

import java.io.File;
import java.text.MessageFormat;

import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;

public class MakeRootProcessor extends AbstractCommandProcessor {

	public MakeRootProcessor(IGitServer server) {
		super(server, ServerConstants.POST, ServerConstants.makeRootPrefix);
	}

	@Override
	protected IProcessResult execute(String actualUrl) {
		File existing = server.findRepositoryUrl(actualUrl);
		if (existing == null) {
			server.createRepository(actualUrl);
			return IProcessResult.Utils.processString(MessageFormat.format(ServerConstants.madeRoot, actualUrl));
		}
		return null;
	}
}

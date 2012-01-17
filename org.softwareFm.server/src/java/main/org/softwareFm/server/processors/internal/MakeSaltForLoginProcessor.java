package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;

public class MakeSaltForLoginProcessor extends AbstractCommandProcessor {

	private final ISaltProcessor saltProcessor;

	public MakeSaltForLoginProcessor(IGitServer server, ISaltProcessor saltProcessor) {
		super(server, ServerConstants.GET, ServerConstants.makeSaltPrefix);
		this.saltProcessor = saltProcessor;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String salt = saltProcessor.makeSalt();
		return IProcessResult.Utils.processString(salt);
	}

}

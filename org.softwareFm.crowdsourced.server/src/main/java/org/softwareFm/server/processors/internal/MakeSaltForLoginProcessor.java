package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;

public class MakeSaltForLoginProcessor extends AbstractCommandProcessor {

	private final ISaltProcessor saltProcessor;

	public MakeSaltForLoginProcessor(ISaltProcessor saltProcessor) {
		super(null, CommonConstants.GET, CommonConstants.makeSaltPrefix);
		this.saltProcessor = saltProcessor;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String salt = saltProcessor.makeSalt();
		return IProcessResult.Utils.processString(salt);
	}

}

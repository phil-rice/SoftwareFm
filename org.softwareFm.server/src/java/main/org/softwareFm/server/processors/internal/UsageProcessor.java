package org.softwareFm.server.processors.internal;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.user.IUser;
import org.softwareFm.utilities.runnable.Callables;

public class UsageProcessor extends AbstractCommandProcessor {

	private final IUser user;
	private final Callable<String> monthGetter;
	private final Callable<Integer> dayGetter;

	public UsageProcessor(IGitServer server, IUser user, Callable<String> monthGetter, Callable<Integer> dayGetter) {
		super(server, ServerConstants.POST, ServerConstants.usagePrefix);
		this.user = user;
		this.monthGetter = monthGetter;
		this.dayGetter = dayGetter;
	}

	@Override
	protected IProcessResult execute(String actualUrl, final Map<String, Object> parameters) {
		String month = Callables.call(monthGetter);
		int day = Callables.call(dayGetter);
		Map<String, Object> initial = user.getProjectDetails(parameters, month);
		Map<String, Object> modified = user.addProjectDetails(parameters, month, day, initial);
		if (modified != null)
			user.saveProjectDetails(parameters, month, modified);
		return IProcessResult.Utils.doNothing();
	}

}

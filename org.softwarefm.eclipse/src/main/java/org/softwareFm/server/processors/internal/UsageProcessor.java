package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.crowdsourced.softwarefm.SoftwareFmConstants;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.user.IProject;
import org.softwareFm.server.user.IProjectTimeGetter;

public class UsageProcessor extends AbstractCommandProcessor {

	private final IProject project;
	private final IProjectTimeGetter projectTimeGetter;

	public UsageProcessor(IGitOperations gitOperations, IProject project, IProjectTimeGetter projectTimeGetter) {
		super(gitOperations, CommonConstants.POST, SoftwareFmConstants.usagePrefix);
		this.project = project;
		this.projectTimeGetter = projectTimeGetter;
	}

	@Override
	protected IProcessResult execute(String actualUrl, final Map<String, Object> parameters) {
		String month = projectTimeGetter.month();
		int day = projectTimeGetter.day();
		String groupId = (String) parameters.get(SoftwareFmConstants.groupIdKey);
		String artifactId = (String) parameters.get(SoftwareFmConstants.artifactIdKey);
		if (groupId == null)
			throw new NullPointerException(parameters.toString());
		if (artifactId == null)
			throw new NullPointerException(parameters.toString());
		project.addProjectDetails(parameters, groupId, artifactId, month, day);
		return IProcessResult.Utils.doNothing();
	}

}

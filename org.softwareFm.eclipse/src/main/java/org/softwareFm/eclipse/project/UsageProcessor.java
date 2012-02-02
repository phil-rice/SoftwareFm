package org.softwareFm.eclipse.project;

import java.util.Map;

import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.internal.AbstractCommandProcessor;

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

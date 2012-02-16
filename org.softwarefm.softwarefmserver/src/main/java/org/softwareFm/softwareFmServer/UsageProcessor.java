package org.softwareFm.softwareFmServer;

import java.util.Map;

import org.apache.log4j.Logger;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.eclipse.IGroupArtifactVersionCallback;
import org.softwareFm.eclipse.IJarToGroupArtifactAndVersion;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.internal.AbstractCommandProcessor;

public class UsageProcessor extends AbstractCommandProcessor {

	private static Logger logger = Logger.getLogger(IProject.class);

	private final IProject project;
	private final IProjectTimeGetter projectTimeGetter;
	private final IJarToGroupArtifactAndVersion jarToGroupArtifactAndVersion;

	public UsageProcessor(IGitOperations gitOperations, IJarToGroupArtifactAndVersion jarToGroupArtifactAndVersion, IProject project, IProjectTimeGetter projectTimeGetter) {
		super(gitOperations, CommonConstants.POST, SoftwareFmConstants.usagePrefix);
		this.jarToGroupArtifactAndVersion = jarToGroupArtifactAndVersion;
		this.project = project;
		this.projectTimeGetter = projectTimeGetter;
	}

	@Override
	protected IProcessResult execute(String actualUrl, final Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, SoftwareFmConstants.digest);
		final String month = projectTimeGetter.thisMonth();
		final int day = projectTimeGetter.day();
		final String digest= (String) parameters.get(SoftwareFmConstants.digest);
		logger.debug("execute" + actualUrl + ", " + parameters + ", " + month + ", " + day);
		jarToGroupArtifactAndVersion.convertJarToGroupArtifactAndVersion(digest, new IGroupArtifactVersionCallback() {
			@Override
			public void process(String groupId, String artifactId, String version) {
				logger.debug("digest: " + digest +", groupId" + groupId + ", artifactId" + artifactId + ", version: " + version + month + ", " + day);
				String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
				project.addProjectDetails(softwareFmId, groupId, artifactId, month, day);
			}
			
			@Override
			public void noData() {
			}
		});
		return IProcessResult.Utils.doNothing();
	}

}

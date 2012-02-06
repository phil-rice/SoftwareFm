package org.softwareFm.softwareFmServer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUser;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IProject;

public class ProjectForServer implements IProject {
	private static Logger logger = Logger.getLogger(IProject.class);

	private final IGitOperations gitOperations;
	private final IUser user;
	private final IFunction1<Map<String, Object>, String> cryptoFn;
	private final IUrlGenerator userUrlGenerator;
	private final Callable<String> cryptoGenerator;

	public ProjectForServer(IGitOperations gitOperations, IFunction1<Map<String, Object>, String> cryptoFn, IUser user, IUrlGenerator userUrlGenerator, Callable<String> cryptoGenerator) {
		this.gitOperations = gitOperations;
		this.cryptoFn = cryptoFn;
		this.user = user;
		this.userUrlGenerator = userUrlGenerator;
		this.cryptoGenerator = cryptoGenerator;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Map<String, List<Integer>>> getProjectDetails(String softwareFm, String month) {
		IFileDescription projectFileDescription = getFileDescriptionForProject(softwareFm, month);
		Map<String, Map<String, List<Integer>>> projectDetails = (Map) gitOperations.getFile(projectFileDescription);
		return projectDetails;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addProjectDetails(String softwareFmId, String groupId, String artifactId, String month, long day) {
		IFileDescription projectFileDescription = getFileDescriptionForProject(softwareFmId, month);
		logger.debug(projectFileDescription);
		Map<String, Object> initialProjectDetails = gitOperations.getFile(projectFileDescription);
		logger.debug("Initial Project Details: " + initialProjectDetails);
		Map<String, Object> groupDetails = (Map<String, Object>) Maps.getOrDefault(initialProjectDetails, groupId, Maps.emptyStringObjectMap());
		List<Long> artifactDetails = (List<Long>) Maps.getOrDefault(groupDetails, artifactId, Collections.<Integer> emptyList());
		if (!artifactDetails.contains(day)) {
			logger.debug("Adding to  Project Details");
			List<Long> newDays = Lists.append(artifactDetails, day);
			Map<String, Object> newGroupData = Maps.with(groupDetails, artifactId, newDays);
			Map<String, Object> newProjectData = Maps.with(initialProjectDetails, groupId, newGroupData);
			gitOperations.put(projectFileDescription, newProjectData);
			logger.debug("New  Project Details: " + newProjectData);
		}
	}

	protected IFileDescription getFileDescriptionForProject(String softwareFmId, String month) {
		Map<String, Object> userDetailMap = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId);
		String userUrl = userUrlGenerator.findUrlFor(userDetailMap);
		logger.debug("User url: " + userUrl); 
		String cryptoKey = Functions.call(cryptoFn, userDetailMap);
		logger.debug("User crypto key: " + cryptoKey); 
		String projectCryptoKey = user.getUserProperty(softwareFmId, cryptoKey, SoftwareFmConstants.projectCryptoKey);
		if (projectCryptoKey == null) {
			logger.debug("Creating project crypto key"); 
			projectCryptoKey = Callables.call(cryptoGenerator);
			user.setUserProperty(softwareFmId, cryptoKey, SoftwareFmConstants.projectCryptoKey, projectCryptoKey);
		}
		logger.debug("Project crypto key: " + cryptoKey); 
		IFileDescription projectFileDescription = IFileDescription.Utils.encrypted(Urls.compose(userUrl, SoftwareFmConstants.projectDirectoryName), month, projectCryptoKey);
		return projectFileDescription;
	}

}

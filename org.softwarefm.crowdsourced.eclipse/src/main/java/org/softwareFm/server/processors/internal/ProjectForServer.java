package org.softwareFm.server.processors.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsourced.softwarefm.SoftwareFmConstants;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.IUser;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.user.IProject;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.runnable.Callables;
import org.softwareFm.utilities.url.IUrlGenerator;
import org.softwareFm.utilities.url.Urls;

public class ProjectForServer implements IProject {

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

	@Override
	public Map<String, Object> getProjectDetails(Map<String, Object> userDetailMap, String month) {
		IFileDescription projectFileDescription = getFileDescriptionForProject(userDetailMap, month);
		Map<String, Object> projectDetails = gitOperations.getFile(projectFileDescription);
		return projectDetails;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addProjectDetails(Map<String, Object> userDetailMap, String groupId, String artifactId, String month, long day) {
		IFileDescription projectFileDescription = getFileDescriptionForProject(userDetailMap, month);
		Map<String, Object> initialProjectDetails = gitOperations.getFile(projectFileDescription);
		Map<String, Object> groupDetails = (Map<String, Object>) Maps.getOrDefault(initialProjectDetails, groupId, Maps.emptyStringObjectMap());
		List<Long> artifactDetails = (List<Long>) Maps.getOrDefault(groupDetails, artifactId, Collections.<Integer> emptyList());
		if (!artifactDetails.contains(day)) {
			List<Long> newDays = Lists.append(artifactDetails, day);
			Map<String, Object> newGroupData = Maps.with(groupDetails, artifactId, newDays);
			Map<String, Object> newProjectData = Maps.with(initialProjectDetails, groupId, newGroupData);
			gitOperations.put(projectFileDescription, newProjectData);
		}
	}

	protected IFileDescription getFileDescriptionForProject(Map<String, Object> userDetailMap, String month) {
		String email = (String) userDetailMap.get(LoginConstants.emailKey);
		if (email == null)
			throw new NullPointerException(userDetailMap.toString());
		String cryptoKey = Functions.call(cryptoFn, userDetailMap);
		String projectCryptoKey = user.getUserProperty(userDetailMap, cryptoKey, SoftwareFmConstants.projectCryptoKey);
		if (projectCryptoKey == null){
			projectCryptoKey =Callables.call(cryptoGenerator);
			user.setUserProperty(userDetailMap, cryptoKey, SoftwareFmConstants.projectCryptoKey, projectCryptoKey);
		}
		String userUrl = userUrlGenerator.findUrlFor(userDetailMap);
		IFileDescription projectFileDescription = IFileDescription.Utils.encrypted(Urls.compose(userUrl, SoftwareFmConstants.projectDirectoryName), month, projectCryptoKey);
		return projectFileDescription;
	}

}

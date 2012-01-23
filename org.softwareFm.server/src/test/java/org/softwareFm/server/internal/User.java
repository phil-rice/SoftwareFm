package org.softwareFm.server.internal;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.softwareFm.server.GetResult;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.ILocalGitClient;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.user.IUser;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.url.IUrlGenerator;

public class User implements IUser {

	public final static String userKey = "user";
	public final static String projectKey = "project";
	public final static String monthKey = "month";
	public final static String cryptoKey = "crypto";
	private final ILocalGitClient gitClient;
	private final IUrlGenerator userGenerator;
	private final IUrlGenerator projectDetailGenerator;

	private final IGitFacard gitFacard = IGitFacard.Utils.makeFacard();
	private final String groupIdKey;
	private final String artifactIdKey;

	public User(ILocalGitClient gitClient, IUrlGenerator userGenerator, IUrlGenerator projectDetailGenerator, String groupIdKey, String artifactIdKey) {
		this.gitClient = gitClient;
		this.userGenerator = userGenerator;
		this.projectDetailGenerator = projectDetailGenerator;
		this.groupIdKey = groupIdKey;
		this.artifactIdKey = artifactIdKey;
	}

	@Override
	public Map<String, Object> getUserDetails(Map<String, Object> userDetails) {
		IFileDescription fileDescription = makeFileDescription(userGenerator, userDetails, ServerConstants.dataFileName);
		GetResult result = gitClient.getFile(fileDescription);
		if (result.found)
			return result.data;
		else
			throw new IllegalArgumentException(userDetails.toString() + "-->" + result);
	}

	@Override
	public void saveUserDetails(Map<String, Object> userDetails, Map<String, Object> data) {
		IFileDescription fileDescription = makeFileDescription(userGenerator, userDetails, ServerConstants.dataFileName);
		File file = fileDescription.getFile(gitClient.getRoot());
		File repositoryRoot = fileDescription.findRepositoryUrl(file);
		if (repositoryRoot == null) {
			File directory = fileDescription.getDirectory(gitClient.getRoot());
			File repositoryDirectory = directory.getParentFile();
			gitFacard.createRepository(repositoryDirectory, "");
		}
		gitClient.post(fileDescription, data);
	}

	private IFileDescription makeFileDescription(IUrlGenerator urlGenerator, Map<String, Object> key, String fileName) {
		String url = urlGenerator.findUrlFor(key);
		String user = (String) key.get(userKey);
		String crypto = (String) key.get(cryptoKey);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, fileName, crypto);
		return fileDescription;
	}

	@Override
	public Map<String, Object> addProjectDetails(Map<String, Object> userDetails, String month, long day, Map<String, Object> data) {
		Object groupId = userDetails.get(groupIdKey);
		String artifactId = (String) userDetails.get(artifactIdKey);
		Map<String, Object> artifactMap = (Map<String, Object>) data.get(groupId);
		if (artifactMap == null)
			return Maps.<String, Object> with(data, groupId, Maps.stringObjectMap(artifactId, Arrays.asList(day)));
		List<Long> artifactList = (List<Long>) artifactMap.get(artifactId);
		if (artifactList.contains(day))
			return null;
		Map<String, Object> newData = Maps.copyMap(data);
		Map<String, Object> newArtifactMap = (Map<String, Object>) newData.get(groupId);
		List<Long> newArtifactList = Lists.append((List<Long>) newArtifactMap.get(artifactId), day);
		newArtifactMap.put(artifactId, newArtifactList);
		return newData;
	}

	@Override
	public Map<String, Object> getProjectDetails(Map<String, Object> userDetails, String month) {
		String url = projectDetailGenerator.findUrlFor(userDetails);
		IFileDescription fileDescription = makeFileDescription(projectDetailGenerator, userDetails, month);
		GetResult result = gitClient.getFile(fileDescription);
			return result.data;
	}

	@Override
	public void saveProjectDetails(Map<String, Object> userDetails, String month, Map<String, Object> data) {
		String url = projectDetailGenerator.findUrlFor(userDetails);
		IFileDescription fileDescription = makeFileDescription(projectDetailGenerator, userDetails, month);
		gitClient.post(fileDescription, data);
	}
}

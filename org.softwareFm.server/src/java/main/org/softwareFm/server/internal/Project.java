package org.softwareFm.server.internal;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.server.GetResult;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.IGitReader;
import org.softwareFm.server.IUser;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.url.IUrlGenerator;

public class Project implements IUser {

	public final static String userKey = "user";
	public final static String projectKey = "project";
	public final static String monthKey = "month";
	public final static String cryptoKey = "crypto";
	private final IGitReader gitReader;
	private final IUrlGenerator userGenerator;
	private final IUrlGenerator projectDetailGenerator;

	private final String groupIdKey;
	private final String artifactIdKey;
	private final IFunction1<Map<String, Object>, String> cryptoFn;
	private final IGitOperations localOperations;

	public Project(IGitReader gitReader, IGitOperations localOperations, IUrlGenerator userGenerator, IUrlGenerator projectDetailGenerator, IFunction1<Map<String, Object>, String> cryptoFn, String groupIdKey, String artifactIdKey) {
		this.gitReader = gitReader;
		this.localOperations = localOperations;
		this.userGenerator = userGenerator;
		this.projectDetailGenerator = projectDetailGenerator;
		this.cryptoFn = cryptoFn;
		this.groupIdKey = groupIdKey;
		this.artifactIdKey = artifactIdKey;
	}

	@Override
	public Map<String, Object> getUserDetails(Map<String, Object> userDetailMap) {
		String crypto = Functions.call(cryptoFn, userDetailMap);
		IFileDescription fileDescription = makeFileDescription(userGenerator, userDetailMap, crypto, CommonConstants.dataFileName);
		Map<String, Object> data = gitReader.getFile(fileDescription);
		if (data == null)
			throw new IllegalArgumentException(userDetailMap.toString());
		else
			return data;
	}

	@Override
	public void saveUserDetails(Map<String, Object> userDetailMap, Map<String, Object> data) {
		String crypto = Functions.call(cryptoFn, userDetailMap);
		IFileDescription fileDescription = makeFileDescription(userGenerator, userDetailMap, crypto, CommonConstants.dataFileName);
		File file = fileDescription.getFile(gitReader.getRoot());
		File repositoryRoot = fileDescription.findRepositoryUrl(file);
		if (repositoryRoot == null) {
			File directory = fileDescription.getDirectory(gitReader.getRoot());
			File repositoryDirectory = directory.getParentFile();
			IGitOperations.Utils.init(localOperations, repositoryRoot);
		}
		localOperations.put(fileDescription, data);
	}asd

	private IFileDescription makeFileDescription(IUrlGenerator urlGenerator, Map<String, Object> key, String crypto, String fileName) {
		String url = urlGenerator.findUrlFor(key);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, fileName, crypto);
		return fileDescription;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> addProjectDetails(Map<String, Object> userDetails, String month, long day, Map<String, Object> initialProjectDetails) {
		Object groupId = userDetails.get(groupIdKey);
		String artifactId = (String) userDetails.get(artifactIdKey);
		if (groupId == null)
			throw new IllegalArgumentException(userDetails.toString());
		if (artifactId == null)
			throw new IllegalArgumentException(userDetails.toString());
		Map<String, Object> artifactMap = (Map<String, Object>) initialProjectDetails.get(groupId);
		if (artifactMap == null)
			return Maps.<String, Object> with(initialProjectDetails, groupId, Maps.stringObjectMap(artifactId, Arrays.asList(day)));
		List<Long> artifactList = (List<Long>) artifactMap.get(artifactId);
		if (artifactList == null)
			artifactList = Lists.newList();
		if (artifactList.contains(day))
			return null;
		Map<String, Object> newData = Maps.copyMap(initialProjectDetails);
		Map<String, Object> newArtifactMap = (Map<String, Object>) newData.get(groupId);
		List<Long> newArtifactList = Lists.append((List<Long>) newArtifactMap.get(artifactId), day);
		newArtifactMap.put(artifactId, newArtifactList);
		return newData;
	}

	@Override
	public Map<String, Object> getProjectDetails(final Map<String, Object> userDetailMap, String month) {
		String projectCrypto = getProjectCrypto(userDetailMap);

		IFileDescription fileDescription = makeFileDescription(projectDetailGenerator, userDetailMap, projectCrypto, month);
		GetResult result = gitReader.getFile(fileDescription);
		return result.data;
	}

	private String getProjectCrypto(final Map<String, Object> userDetailMap) {
		final Map<String, Object> userDetails = getUserDetails(userDetailMap);
		String projectCrypto = (String) Maps.findOrCreate(userDetails, LoginConstants.projectCryptoKey, new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				String result = Crypto.makeKey();
				Map<String, Object> modifiedUserDetails = Maps.with(userDetails, LoginConstants.projectCryptoKey, result);
				saveUserDetails(userDetailMap, modifiedUserDetails);
				return result;
			}
		});
		return projectCrypto;
	}

	@Override
	public void saveProjectDetails(Map<String, Object> userDetailMap, String month, Map<String, Object> data) {
		String projectCrypto = getProjectCrypto(userDetailMap);
		IFileDescription fileDescription = makeFileDescription(projectDetailGenerator, userDetailMap, projectCrypto, month);
		gitReader.post(fileDescription, data);
	}
}

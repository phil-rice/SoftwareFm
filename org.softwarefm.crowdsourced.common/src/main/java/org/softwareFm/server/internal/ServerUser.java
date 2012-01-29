package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.IUser;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.url.IUrlGenerator;

public class ServerUser implements IUser {

	private final IGitOperations gitOperations;
	private final IUrlGenerator userUrlGenerator;
	private final IFunction1<String, String> findRepositoryRoot;

	public ServerUser(IGitOperations gitOperations, IUrlGenerator userUrlGenerator, IFunction1<String, String> findRepositoryRoot) {
		this.gitOperations = gitOperations;
		this.userUrlGenerator = userUrlGenerator;
		this.findRepositoryRoot = findRepositoryRoot;
	}

	@Override
	public <T> T getUserProperty(Map<String, Object> userDetails, String cryptoKey, String property) {
		String url = userUrlGenerator.findUrlFor(userDetails);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, cryptoKey);
		Map<String, Object> data = gitOperations.getFile(fileDescription);
		@SuppressWarnings("unchecked")
		T result = (T) data.get(property);
		return result;
	}

	@Override
	public <T> void setUserProperty(java.util.Map<String, Object> userDetails, String cryptoKey, String property, T value) {
		String url = userUrlGenerator.findUrlFor(userDetails);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, cryptoKey);
		File repositoryUrl = fileDescription.findRepositoryUrl(gitOperations.getRoot());
		if (repositoryUrl == null){
			String root = Functions.call(findRepositoryRoot, url);
			gitOperations.init(root);
		}
		IFileDescription.Utils.merge(gitOperations, fileDescription, Maps.stringObjectMap(property, value));
		gitOperations.clearCaches();
	}
}

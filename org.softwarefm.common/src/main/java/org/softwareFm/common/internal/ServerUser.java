package org.softwareFm.common.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;

public class ServerUser implements IUser {

	private final IGitOperations gitOperations;
	private final IUrlGenerator userUrlGenerator;
	private final IFunction1<String, String> userRepositoryDefn;

	public ServerUser(IGitOperations gitOperations, IUrlGenerator userUrlGenerator, IFunction1<String, String> userRepositoryDefn) {
		this.gitOperations = gitOperations;
		this.userUrlGenerator = userUrlGenerator;
		this.userRepositoryDefn = userRepositoryDefn;
	}

	@Override
	public <T> T getUserProperty(String softwareFmId, String cryptoKey, String property) {
		String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		IFileDescription fileDescription1 = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, cryptoKey);
		IFileDescription fileDescription = fileDescription1;
		Map<String, Object> data = gitOperations.getFile(fileDescription);
		if (data == null)
			return null;
		@SuppressWarnings("unchecked")
		T result = (T) data.get(property);
		return result;
	}

	@Override
	public <T> void setUserProperty(String softwareFmId, String cryptoKey, String property, T value) {
		String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		IFileDescription fileDescription1 = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, cryptoKey);
		IFileDescription fileDescription = fileDescription1;
		File repositoryUrl = fileDescription.findRepositoryUrl(gitOperations.getRoot());
		if (repositoryUrl == null) {
			String root = Functions.call(userRepositoryDefn, url);
			gitOperations.init(root);
		}
		IFileDescription.Utils.merge(gitOperations, fileDescription, Maps.stringObjectMap(property, value));
		gitOperations.clearCaches();
	}

	@Override
	public void refresh(String softwareFmId) {
		throw new UnsupportedOperationException();
	}
}

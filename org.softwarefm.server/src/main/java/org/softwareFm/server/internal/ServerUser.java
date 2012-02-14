package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.url.IUrlGenerator;

public class ServerUser implements IUser {

	private final IGitOperations gitOperations;
	private final IUrlGenerator userUrlGenerator;
	private final IFunction1<String, String> userRepositoryDefn;
	private final Map<String, Callable<Object>> defaultValues;

	public ServerUser(IGitOperations gitOperations, IUrlGenerator userUrlGenerator, IFunction1<String, String> userRepositoryDefn, Map<String, Callable<Object>>  defaultValues) {
		this.gitOperations = gitOperations;
		this.userUrlGenerator = userUrlGenerator;
		this.userRepositoryDefn = userRepositoryDefn;
		this.defaultValues = defaultValues;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUserProperty(String softwareFmId, String cryptoKey, String property) {
		String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, cryptoKey);
		Map<String, Object> data = gitOperations.getFile(fileDescription);
		if (data != null) {
			T result = (T) data.get(property);
			if (result != null)
				return result;

		}
		Callable<T> callable = (Callable<T>) defaultValues.get(property);
		if (callable == null)
			return null;
		T result = Callables.call(callable);
		setUserProperty(softwareFmId, cryptoKey, property, result);
		return result;
	}

	@Override
	public <T> void setUserProperty(String softwareFmId, String cryptoKey, String property, T value) {
		if (softwareFmId == null || cryptoKey == null || property == null)
			throw new IllegalStateException(softwareFmId + ", " + cryptoKey + ", " + property + ", " + value);
		String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, cryptoKey);
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

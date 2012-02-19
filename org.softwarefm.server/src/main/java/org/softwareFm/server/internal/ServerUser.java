/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
	public <T> T getUserProperty(String softwareFmId, String userCrypto, String property) {
		String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, userCrypto);
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
		setUserProperty(softwareFmId, userCrypto, property, result);
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
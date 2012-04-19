/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.IResponse;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.httpClient.internal.MemoryResponseCallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.jarAndClassPath.api.IUsageStrategy;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;

public class UsageStrategy implements IUsageStrategy {
	private final IUrlGenerator userGenerator;
	private final IContainer container;
	private final File root;
	private final long timeOutMs;

	public UsageStrategy(IContainer container, IUrlGenerator userGenerator, long timeOutMs) {
		this.container = container;
		this.userGenerator = userGenerator;
		this.timeOutMs = timeOutMs;
		this.root = container.gitOperations().getRoot();
	}

	@Override
	public ITransaction<?> using(final String softwareFmId, final String digest, final IResponseCallback callback) {
		return container.accessWithCallback(IHttpClient.class, new IFunction1<IHttpClient, IResponse>() {
			@Override
			public IResponse apply(final IHttpClient client) throws Exception {
				MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
				client.post(JarAndPathConstants.usagePrefix).//
						addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
						addParam(JarAndPathConstants.digest, digest).//
						execute(memoryCallback).get(timeOutMs, TimeUnit.MILLISECONDS);
				return memoryCallback.response;
			}
		}, new ICallback<IResponse>() {
			@Override
			public void process(IResponse response) throws Exception {
				callback.process(response);

			}
		});

	}

	@Override
	public Map<String, Object> myProjectData(final String softwareFmId, final String crypto) {
		return container.access(IGitLocal.class, new IFunction1<IGitLocal, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(IGitLocal gitLocal) throws Exception {
				String userUrl = userGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
				Map<String, Object> userDataResult = gitLocal.getFile(IFileDescription.Utils.encrypted(userUrl, CommonConstants.dataFileName, crypto));

				if (userDataResult == null)
					throw new IllegalArgumentException(softwareFmId);
				String projectCrypto = (String) userDataResult.get(JarAndPathConstants.projectCryptoKey);

				File directory = new File(root, Urls.compose(userUrl, JarAndPathConstants.projectDirectoryName));
				Map<String, Object> result = Maps.newMap();
				for (File file : Lists.list(directory.listFiles())) {
					Map<String, Object> map = Json.mapFromEncryptedFile(file, projectCrypto);
					result.put(file.getName(), map);
				}
				return result;
			}
		}).get();

	}
}
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.IResponse;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.jarAndClassPath.api.IUsageStrategy;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.jarAndClassPath.constants.JarAndPathMessages;

public class UsageStrategy implements IUsageStrategy {
	private final IServiceExecutor serviceExecutor;
	private final IUrlGenerator userGenerator;
	private final IContainer container;
	private final File root;

	public UsageStrategy(IContainer container, IServiceExecutor serviceExecutor, IUrlGenerator userGenerator) {
		this.container = container;
		this.userGenerator = userGenerator;
		this.serviceExecutor = serviceExecutor;
		this.root = container.gitOperations().getRoot();
	}

	@Override
	public Future<?> using(final String softwareFmId, final String digest, final IResponseCallback callback) {
		return container.access(IHttpClient.class, new IFunction1<IHttpClient, Future<?>>() {
			@Override
			public Future<?> apply(final IHttpClient client) throws Exception {
				return serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
					@Override
					public Void apply(final IMonitor monitor) throws Exception {
						monitor.beginTask(JarAndPathMessages.recordingUsage, 2);
						client.post(JarAndPathConstants.usagePrefix).//
								addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
								addParam(JarAndPathConstants.digest, digest).//
								execute(new IResponseCallback() {
									@Override
									public void process(IResponse response) {
										try {
											callback.process(response);
										} finally {
											monitor.done();
										}
									}
								}).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
						return null;
					}
				});
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
		});

	}
}
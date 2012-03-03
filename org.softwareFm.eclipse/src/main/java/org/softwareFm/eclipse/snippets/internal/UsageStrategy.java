/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.snippets.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.response.IResponse;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.monitor.IMonitor;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.EclipseMessages;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.usage.IUsageStrategy;

public class UsageStrategy implements IUsageStrategy {
	private final IHttpClient client;
	private final IGitLocal gitLocal;
	private final IServiceExecutor serviceExecutor;
	private final IUrlGenerator userGenerator;

	public UsageStrategy(IHttpClient client, IServiceExecutor serviceExecutor, IGitLocal gitLocal, IUrlGenerator userGenerator) {
		this.client = client;
		this.userGenerator = userGenerator;
		this.gitLocal = gitLocal;
		this.serviceExecutor = serviceExecutor;
	}

	@Override
	public Future<?> using(final String softwareFmId, final String digest, final IResponseCallback callback) {
		return serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
			@Override
			public Void apply(final IMonitor monitor) throws Exception {
				monitor.beginTask(EclipseMessages.recordingUsage, 2);
				client.post(SoftwareFmConstants.usagePrefix).//
						addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
						addParam(SoftwareFmConstants.digest, digest).//
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

	@Override
	public Map<String, Object> myProjectData(final String softwareFmId, final String crypto) {
		File root = gitLocal.getRoot();
		String userUrl = userGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		Map<String, Object> userDataResult = gitLocal.getFile(IFileDescription.Utils.encrypted(userUrl, CommonConstants.dataFileName, crypto));

		if (userDataResult == null)
			throw new IllegalArgumentException(softwareFmId);
		String projectCrypto = (String) userDataResult.get(SoftwareFmConstants.projectCryptoKey);

		File directory = new File(root, Urls.compose(userUrl, SoftwareFmConstants.projectDirectoryName));
		Map<String, Object> result = Maps.newMap();
		for (File file : Lists.list(directory.listFiles())) {
			Map<String, Object> map = Json.mapFromEncryptedFile(file, projectCrypto);
			result.put(file.getName(), map);
		}
		return result;
	}
}
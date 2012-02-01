package org.softwareFm.collections.explorer.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.collections.explorer.IUsageStrategy;
import org.softwareFm.crowdsourced.softwarefm.SoftwareFmConstants;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitLocal;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwareFm.utilities.url.IUrlGenerator;
import org.softwareFm.utilities.url.Urls;

public class UsageStrategy implements IUsageStrategy {
	private final IHttpClient client;
	private final IGitLocal gitLocal;
	private final IServiceExecutor serviceExecutor;
	private final IUrlGenerator userGenerator;

	public UsageStrategy(IHttpClient client, IServiceExecutor serviceExecutor, IGitLocal gitLocal, IUrlGenerator userGenerator, IUrlGenerator projectGenerator) {
		this.client = client;
		this.userGenerator = userGenerator;
		this.gitLocal = gitLocal;
		this.serviceExecutor = serviceExecutor;
	}

	@Override
	public Future<?> using(final String softwareFmId, final String groupId, final String artifactId, final IResponseCallback callback) {
		System.out.println("Usage: " + softwareFmId + ", " + groupId + ", " + artifactId);
		return serviceExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				client.post(SoftwareFmConstants.usagePrefix).//
						addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
						addParam(SoftwareFmConstants.groupIdKey, groupId).//
						addParam(SoftwareFmConstants.artifactIdKey, artifactId).//
						execute(callback).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
				return null;
			}
		});
	}

	@Override
	public Map<String, Object> myProjectData(final String softwareFmId, final String crypto) {
		File root = gitLocal.getRoot();
		String userUrl = userGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		Map<String, Object> userDataResult = gitLocal.getFile(IFileDescription.Utils.encrypted(userUrl, CommonConstants.dataFileName, crypto));

		if (userDataResult==null)
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
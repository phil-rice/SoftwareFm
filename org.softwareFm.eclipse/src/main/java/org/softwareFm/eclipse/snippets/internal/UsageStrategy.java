package org.softwareFm.eclipse.snippets.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
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
		return serviceExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				client.post(SoftwareFmConstants.usagePrefix).//
						addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
						addParam(SoftwareFmConstants.digest, digest).//
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
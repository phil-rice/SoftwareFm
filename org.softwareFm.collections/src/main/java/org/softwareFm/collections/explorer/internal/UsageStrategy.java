package org.softwareFm.collections.explorer.internal;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.collections.explorer.IUsageStrategy;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwareFm.utilities.strings.Strings;
import org.softwareFm.utilities.url.IUrlGenerator;

public class UsageStrategy implements IUsageStrategy {
	private final IHttpClient client;
	private final String artifactIdKey;
	private final String groupIdKey;
	private final IGitServer gitServer;
	private final IServiceExecutor serviceExecutor;
	private final IUrlGenerator projectGenerator;
	private final IUrlGenerator userGenerator;

	public UsageStrategy(IHttpClient client, IServiceExecutor serviceExecutor, IGitServer gitServer,IUrlGenerator userGenerator,  IUrlGenerator projectGenerator, String artifactIdKey, String groupIdKey) {
		this.client = client;
		this.userGenerator = userGenerator;
		this.projectGenerator = projectGenerator;
		this.artifactIdKey = artifactIdKey;
		this.groupIdKey = groupIdKey;
		this.gitServer = gitServer;
		this.serviceExecutor = serviceExecutor;
	}

	@Override
	public Future<?> using(final String softwareFmId, final String groupId, final String artifactId, final IResponseCallback callback) {
		System.out.println("Usage: " + softwareFmId+", " + groupId +", " + artifactId);
		return serviceExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				client.post(ServerConstants.usagePrefix).//
						addParam(ServerConstants.softwareFmIdKey, softwareFmId).//
						addParam(groupIdKey, groupId).//
						addParam(artifactIdKey, artifactId).//
						execute(callback).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
				return null;
			}
		});
	}

	@Override
	public Map<String, Object> myProjectData(final String softwareFmId, final String crypto) {
		File root = gitServer.getRoot();
		String userUrl = userGenerator.findUrlFor(Maps.stringObjectMap(ServerConstants.softwareFmIdKey, softwareFmId));
		String projectUrl = projectGenerator.findUrlFor(Maps.stringObjectMap(ServerConstants.softwareFmIdKey, softwareFmId));
		List<String> segments = Strings.splitIgnoreBlanks(projectUrl, "/");
		String repositoryUrl = segments.get(0) +"/"+ segments.get(1) +"/" + segments.get(2)+"/"+ segments.get(3);
		IGitServer.Utils.cloneOrPull(gitServer, repositoryUrl);
		GetResult userDataResult = gitServer.getFile(IFileDescription.Utils.encrypted(userUrl, ServerConstants.dataFileName, crypto));
		if (!userDataResult.found)
			throw new IllegalArgumentException(softwareFmId);
		String projectCrypto = (String) userDataResult.data.get(ServerConstants.projectCryptoKey);
		
		File directory = new File(root, projectUrl);
		Map<String, Object> result = Maps.newMap();
		for (File file : Lists.list(directory.listFiles())) {
			Map<String, Object> map = Json.mapFromEncryptedFile(file, projectCrypto);
			result.put(file.getName(), map);
		}
		return result;
	}
}
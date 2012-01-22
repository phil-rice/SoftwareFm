package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.url.Urls;

public class ImportGitRepositoryFacard implements IRepositoryFacard {

	private final IGitServer remoteGitServer;

	public ImportGitRepositoryFacard(IGitServer remoteGitServer) {
		this.remoteGitServer = remoteGitServer;
	}

	@Override
	public Future<?> get(String url, IRepositoryFacardCallback callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Future<?> delete(String url, IResponseCallback callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Future<?> post(String url, Map<String, Object> map, IResponseCallback callback) {
		File file = new File(remoteGitServer.getRoot(), Urls.compose(url, ServerConstants.dataFileName));
		File directory = file.getParentFile();
		directory.mkdirs();
		String text = Json.toString(map);
		Files.setText(file, text);
		return Futures.doneFuture(null);
	}

	@Override
	public Future<?> makeRoot(String url, IResponseCallback callback) {
		remoteGitServer.createRepository(url);
		return Futures.doneFuture(null);
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void clearCaches() {
	}

	@Override
	public void clearCache(String url) {
		throw new UnsupportedOperationException();
	}

}

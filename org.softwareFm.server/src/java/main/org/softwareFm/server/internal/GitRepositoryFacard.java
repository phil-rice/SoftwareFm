/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.requests.MemoryResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ISoftwareFmClient;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.maps.UrlCache;
import org.softwareFm.utilities.runnable.Callables;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwareFm.utilities.strings.Urls;

/** This class reads from the local file system. If the file isn't held locally, it asks for a git download, then returns the file. */
public class GitRepositoryFacard implements ISoftwareFmClient {

	private final IServiceExecutor serviceExecutor;
	private final IGitServer localGit;
	private final IHttpClient httpClient;
	private final long staleCacheTime;
	private final Map<File, Long> lastPullTimeMap = Maps.newMap();
	private long lastAboveRepositoryClearTime;
	private final UrlCache<GetResult> aboveRepositoryCache = new UrlCache<GetResult>();
	private final long aboveRepositorytStaleCacheTime;

	public GitRepositoryFacard(IHttpClient httpClient, IServiceExecutor serviceExecutor, IGitServer localGit, long staleCacheTime, long aboveRepositorytStaleCacheTime) {
		this.httpClient = httpClient;
		this.serviceExecutor = serviceExecutor;
		this.localGit = localGit;
		this.staleCacheTime = staleCacheTime;
		this.aboveRepositorytStaleCacheTime = aboveRepositorytStaleCacheTime;
		lastAboveRepositoryClearTime = System.currentTimeMillis();
	}

	@Override
	public void clearCaches() {
		lastPullTimeMap.clear();
	}

	@Override
	public Future<?> makeRoot(final String url, final IResponseCallback callback) {
		return serviceExecutor.submit(new Callable<GetResult>() {
			@Override
			public GetResult call() throws Exception {
				MemoryResponseCallback<Object, Object> memoryCallback = IResponseCallback.Utils.memoryCallback();
				String fullUrl = Urls.compose(ServerConstants.makeRootPrefix, url);
				httpClient.post(fullUrl).execute(memoryCallback).get();
				aboveRepositoryCache.clear(url);
				IGitServer.Utils.cloneOrPull(localGit, url);
				callback.process(memoryCallback.response);
				return null;
			}
		});
	}

	@Override
	public Future<?> get(final String url, final IRepositoryFacardCallback callback) {
		final AtomicBoolean callbackHasBeenCalled = new AtomicBoolean();
		Future<GetResult> future = serviceExecutor.submit(new Callable<GetResult>() {
			@Override
			public GetResult call() throws Exception {
				if (aboveRepositoryCache.containsKey(url)) {
					if (System.currentTimeMillis() > lastAboveRepositoryClearTime + aboveRepositorytStaleCacheTime) {
						long now = System.currentTimeMillis();
						lastAboveRepositoryClearTime = now;
						aboveRepositoryCache.clear();
					} else {
						GetResult result = aboveRepositoryCache.get(url);
						processCallback(callbackHasBeenCalled, callback, url, result);
						return result;
					}
				}
				File localRepositoryRoot = localGit.findRepositoryUrl(url);
				if (localRepositoryRoot != null) {// we have found something, and it is in a repository
					long lastPull = Maps.findOrCreate(lastPullTimeMap, localRepositoryRoot, Callables.time());
					long now = System.currentTimeMillis();
					boolean needToPull = now > lastPull + staleCacheTime;
					if (needToPull) {
						String url = Files.offset(localGit.getRoot(), localRepositoryRoot);
						localGit.pull(url);
						lastPullTimeMap.put(localRepositoryRoot, now);
					}
					GetResult result = localGit.localGet(url);
					processCallback(callbackHasBeenCalled, callback, url, result);
					return result;
				}
				// well we didn't find a repository
				GetResult result = findRepositoryBaseOrAboveRepositoryData(url, new IGetCallback() {
					@Override
					public GetResult repositoryBase(String repositoryBase) {
						localGit.clone(repositoryBase);
						GetResult afterCloneResult = localGit.localGet(url);
						return afterCloneResult;
					}

					@Override
					public void invalidResponse(int statusCode, String message) {
					}

					@Override
					public GetResult aboveRepositoryData(Map<String, Object> data) {
						GetResult result = aboveRepositoryCache.findOrCreate(url, Callables.value(GetResult.create(data)));
						return result;
					}
				}).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
				processCallback(callbackHasBeenCalled, callback, url, result);
				return result;
			}
		});
		return future;
	}

	private void processCallback(AtomicBoolean callbackHasBeenCalled, final IRepositoryFacardCallback callback, String url, GetResult result) throws Exception {
		if (callbackHasBeenCalled.compareAndSet(false, true)) {
			boolean found = result.found && !result.data.isEmpty();
			int status = found ? ServerConstants.okStatusCode : ServerConstants.notFoundStatusCode;
			String message = found ? ServerConstants.foundMessage : ServerConstants.notFoundMessage;
			IResponse makeResponse = IResponse.Utils.create(url, status, message);
			callback.process(makeResponse, result.data);
		}
	}

	@Override
	public Future<?> post(final String url, final Map<String, Object> map, final IResponseCallback callback) {
		lastPullTimeMap.clear();// we can do better and only clear relevant caches..
		File existing = localGit.findRepositoryUrl(url);
		if (existing == null)
			throw new IllegalStateException(MessageFormat.format(ServerConstants.cannotPostWhenLocalRepositoryDoesntExist, url));
		return serviceExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				MemoryResponseCallback<Object, Object> memoryCallback = IResponseCallback.Utils.memoryCallback();
				httpClient.post(url).addParams(ServerConstants.dataParameterName, Json.toString(map)).execute(memoryCallback).get();
				localGit.pull(url);
				callback.process(memoryCallback.response);
				return null;
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public Future<GetResult> findRepositoryBaseOrAboveRepositoryData(String url, final IGetCallback callback) {
		final AtomicReference<GetResult> result = new AtomicReference<GetResult>();
		Future<Object> rawFuture = (Future<Object>) httpClient.get(url).execute(new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				if (response.statusCode() != ServerConstants.okStatusCode) {
					callback.invalidResponse(response.statusCode(), response.asString());
					result.set(GetResult.notFound());
				} else {
					Map<String, Object> map = Json.mapFromString(response.asString());
					if (map.containsKey(ServerConstants.repoUrlKey))
						result.set(callback.repositoryBase((String) map.get(ServerConstants.repoUrlKey)));
					else if (map.containsKey(ServerConstants.dataKey))
						result.set(callback.aboveRepositoryData((Map<String, Object>) map.get(ServerConstants.dataKey)));
					else {
						callback.invalidResponse(response.statusCode(), MessageFormat.format(ServerConstants.cannotParseReplyFromGet, map));
						result.set(GetResult.notFound());
					}
				}
			}
		});
		return Futures.transformed(rawFuture, new IFunction1<Object, GetResult>() {
			@Override
			public GetResult apply(Object from) throws Exception {
				return result.get();
			}
		});
	}

	@Override
	public void addHeader(String name, String value) {
	}

	@Override
	public Future<?> delete(String url, IResponseCallback callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void shutdown() {
		serviceExecutor.shutdownAndAwaitTermination(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		httpClient.shutdown();
	}
}
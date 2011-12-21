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
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwareFm.utilities.strings.Urls;

/** This class reads from the local file system. If the file isn't held locally, it asks for a git download, then returns the file. */
public class GitRepositoryFacard implements ISoftwareFmClient {

	private final IServiceExecutor serviceExecutor;
	private final IGitServer localGit;
	private final Map<String, GetResult> getCache = Maps.newMap();
	private final IHttpClient httpClient;
	private final long staleCacheTime;

	public GitRepositoryFacard(IHttpClient httpClient, IServiceExecutor serviceExecutor, IGitServer localGit, long staleCacheTime) {
		this.httpClient = httpClient;
		this.serviceExecutor = serviceExecutor;
		this.localGit = localGit;
		this.staleCacheTime = staleCacheTime;
	}

	@Override
	public void clearCaches() {
		getCache.clear();
	}

	@Override
	public Future<?> makeRoot(final String url, final IResponseCallback callback) {
		return serviceExecutor.submit(new Callable<GetResult>() {
			@Override
			public GetResult call() throws Exception {
				MemoryResponseCallback<Object, Object> memoryCallback = IResponseCallback.Utils.memoryCallback();
				String fullUrl = Urls.compose(ServerConstants.makeRootPrefix, url);
				httpClient.post(fullUrl).execute(memoryCallback).get();
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
				Callable<GetResult> callable = new Callable<GetResult>() {
					@Override
					public GetResult call() throws Exception {
						GetResult firstResult = localGit.localGet(url);
						if (firstResult.found) {
							File localRepositoryUrl = localGit.findRepositoryUrl(url);
							if (localRepositoryUrl != null) {// we have found something, and it is in a repository
								File repoDirectory = new File(localRepositoryUrl, ServerConstants.DOT_GIT);
								long lastModified = repoDirectory.lastModified();
								if (System.currentTimeMillis() >= lastModified + staleCacheTime) {

								} else {
									processCallback(callbackHasBeenCalled, callback, url, firstResult);
									return firstResult;
								}
							}
						}
						GetResult result = findRepositoryBaseOrAboveRepositoryData(url, new IGetCallback() {
							@Override
							public GetResult repositoryBase(String repositoryBase) {
								IGitServer.Utils.cloneOrPull(localGit, repositoryBase);
								GetResult afterCloneResult = localGit.localGet(url);
								return afterCloneResult;
							}

							@Override
							public void invalidResponse(int statusCode, String message) {
							}

							@Override
							public GetResult aboveRepositoryData(Map<String, Object> data) {
								return GetResult.create(data);
							}
						}).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
						processCallback(callbackHasBeenCalled, callback, url, result);
						return result;
					}
				};
				GetResult result = Maps.findOrCreate(getCache, url, callable);
				if (System.currentTimeMillis() > result.created + staleCacheTime) {
					result = callable.call();
					getCache.put(url, result);
				}
				if (!callbackHasBeenCalled.get()) // probably we are here because the item was in the cache, but this also works if the round trip above returns really quicky
					processCallback(callbackHasBeenCalled, callback, url, result);
				return result;
			}
		});
		return future;
	}

	private void processCallback(AtomicBoolean callbackHasBeenCalled, final IRepositoryFacardCallback callback, String url, GetResult result) throws Exception {
		if (callbackHasBeenCalled.compareAndSet(false, true)) {
			boolean found = result.found;
			int status = found ? ServerConstants.okStatusCode : ServerConstants.notFoundStatusCode;
			String message = found ? ServerConstants.foundMessage : ServerConstants.notFoundMessage;
			IResponse makeResponse = IResponse.Utils.create(url, status, message);
			callback.process(makeResponse, result.data);
		}
	}

	@Override
	public Future<?> post(final String url, final Map<String, Object> map, final IResponseCallback callback) {
		getCache.clear();// we can do better and only clear relevant caches..
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
		serviceExecutor.shutdown();
		httpClient.shutdown();
	}
}
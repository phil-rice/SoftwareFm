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

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.requests.MemoryResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ISoftwareFmClient;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.services.IServiceExecutor;

/** This class reads from the local file system. If the file isn't held locally, it asks for a git download, then returns the file. */
public class GitRepositoryFacard implements ISoftwareFmClient {

	private final IServiceExecutor serviceExecutor;
	private final IGitServer localGit;
	private final Map<String, GetResult> getCache = Maps.newMap();
	private final IHttpClient httpClient;

	public GitRepositoryFacard(IHttpClient httpClient, IServiceExecutor serviceExecutor, IGitServer localGit) {
		this.httpClient = httpClient;
		this.serviceExecutor = serviceExecutor;
		this.localGit = localGit;
	}

	@Override
	public Future<?> makeRoot(final String url, final IResponseCallback callback) {
		return serviceExecutor.submit(new Callable<GetResult>() {
			@Override
			public GetResult call() throws Exception {
				MemoryResponseCallback<Object, Object> memoryCallback = IResponseCallback.Utils.memoryCallback();
				httpClient.post(ServerConstants.makeRootPrefix + url).execute(memoryCallback).get();
				localGit.clone(url);
				callback.process(memoryCallback.response);
				return null;
			}
		});
	}

	@Override
	public Future<?> get(final String url, final IRepositoryFacardCallback callback) {
		return serviceExecutor.submit(new Callable<GetResult>() {
			@Override
			public GetResult call() throws Exception {
				GetResult result = Maps.findOrCreate(getCache, url, new Callable<GetResult>() {
					@Override
					public GetResult call() throws Exception {
						GetResult firstResult = localGit.localGet(url);
						if (firstResult.found) {
							processCallback(firstResult);
							return firstResult;
						}
						MemoryCallback<String> memory = ICallback.Utils.<String> memory();
						findRepositoryBase(url, memory).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
						String repositoryBase = memory.getOnlyResult();
						if (repositoryBase == null) {// returning null because the remote server has never heard of it
							processCallback(firstResult);
							return GetResult.create(null);
						}
						localGit.clone(repositoryBase);
						GetResult afterCloneResult = localGit.localGet(url);
						processCallback(afterCloneResult);
						return afterCloneResult;
					}

					private void processCallback(GetResult result) throws Exception {
						boolean found = result.found;
						int status = found ? ServerConstants.okStatusCode : ServerConstants.notFoundStatusCode;
						String message = found ? ServerConstants.foundMessage : ServerConstants.notFoundMessage;
						IResponse makeResponse = IResponse.Utils.create(url, status, message);
						callback.process(makeResponse, result.data);
					}
				});
				return result;
			}
		});
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
	public Future<?> findRepositoryBase(String url, final ICallback<String> callback) {
		// we can speed this up by using the fact that urls have structure, and if a parent has been investigated, we know the result.
		return httpClient.get(ServerConstants.findRepositoryBasePrefix + "/" + url).execute(new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				String result = response.statusCode() == ServerConstants.okStatusCode ? response.asString() : null;
				ICallback.Utils.call(callback, result);
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
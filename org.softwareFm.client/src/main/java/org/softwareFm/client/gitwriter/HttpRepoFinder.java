/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.client.gitwriter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.requests.MemoryResponseCallback;
import org.softwareFm.client.http.response.IResponse;
import org.softwareFm.common.IRepoFinder;
import org.softwareFm.common.RepoDetails;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.strings.Strings;

public class HttpRepoFinder implements IRepoFinder {

	private final IHttpClient client;
	private final long timeOutInMs;

	public HttpRepoFinder(IHttpClient client, long timeOutInMs) {
		this.client = client;
		this.timeOutInMs = timeOutInMs;
	}

	@Override
	public void clearCaches() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public RepoDetails findRepoUrl(String url) {
		try {
			MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
			client.get(url).execute(memoryCallback).get(timeOutInMs, TimeUnit.MILLISECONDS);
			IResponse response = memoryCallback.response;
			if (CommonConstants.okStatusCodes.contains(response.statusCode())) {
				Map<String, Object> data = Json.mapFromString(response.asString());
				if (data.containsKey(CommonConstants.repoUrlKey))
					return RepoDetails.repositoryUrl(Strings.nullSafeToString(data.get(CommonConstants.repoUrlKey)));
				if (!data.containsKey(CommonConstants.dataParameterName))
					throw new IllegalStateException(data.toString());
				return RepoDetails.aboveRepo((Map<String, Object>) data.get(CommonConstants.dataKey));
			} else
				throw new IllegalStateException(response.toString());
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void clearCache(String url) {
	}
}
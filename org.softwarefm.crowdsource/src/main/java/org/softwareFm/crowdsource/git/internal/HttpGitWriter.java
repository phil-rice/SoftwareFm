/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.git.internal;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class HttpGitWriter {

	private final IHttpClient httpClient;
	private final long delayMs;

	public HttpGitWriter(IHttpClient httpClient) {
		this(httpClient, CommonConstants.clientTimeOut);
	}

	public HttpGitWriter(IHttpClient httpClient, long delayMs) {
		this.httpClient = httpClient;
		this.delayMs = delayMs;
	}

	public void init(String url, String commitMessage) {
		try {
			httpClient.post(Urls.compose(CommonConstants.makeRootPrefix, url)).execute(IResponseCallback.Utils.noCallback()).get(delayMs, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public void put(IFileDescription fileDescription, Map<String, Object> data, String commitMessage) {
		try {
			httpClient.post(fileDescription.url()).//
					addParam(CommonConstants.dataParameterName, Json.toString(data)).//
					execute(IResponseCallback.Utils.noCallback()).//
					get(delayMs, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public void delete(IFileDescription fileDescription, String commitMessage) {
		try {
			httpClient.delete(fileDescription.url()).execute(IResponseCallback.Utils.noCallback()).get(delayMs, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
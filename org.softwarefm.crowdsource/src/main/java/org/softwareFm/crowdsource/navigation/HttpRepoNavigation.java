/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.navigation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.httpClient.internal.MemoryResponseCallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallbackWithFail;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class HttpRepoNavigation implements IRepoNavigation {

	private final IContainer container;

	public HttpRepoNavigation(IContainer container) {
		this.container = container;
	}

	@Override
	public ITransaction<Map<String, List<String>>> navigationData(final String url, final ICallback<Map<String, List<String>>> callback) {
		return container.access(IHttpClient.class, new IFunction1<IHttpClient, Map<String, List<String>>>() {
			@Override
			public Map<String, List<String>> apply(IHttpClient client) throws Exception {
				MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
				client.post(Urls.composeWithSlash(CommonConstants.navigationPrefix, url)).execute(memoryCallback).get();
				boolean ok = CommonConstants.okStatusCodes.contains(memoryCallback.response.statusCode());
				@SuppressWarnings("unchecked")
				Map<String, List<String>> result = (Map<String, List<String>>) (ok ? Json.parse(memoryCallback.response.asString()) : Collections.emptyMap());
				if (!ok && (callback instanceof ICallbackWithFail<?>))
					((ICallbackWithFail<Map<String, List<String>>>) callback).fail();
				else
					callback.process(result);
				return result;
			}
		});
	}

}
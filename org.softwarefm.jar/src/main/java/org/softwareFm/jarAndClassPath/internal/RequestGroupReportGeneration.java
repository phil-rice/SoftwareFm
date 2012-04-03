/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.internal;

import java.util.concurrent.Future;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.IResponse;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.IHasCache;
import org.softwareFm.jarAndClassPath.api.IRequestGroupReportGeneration;

public class RequestGroupReportGeneration implements IRequestGroupReportGeneration {

	private final IResponseCallback callback;
	private final IHasCache cache;
	private final IContainer container;

	public RequestGroupReportGeneration(IContainer container, IResponseCallback callback) {
		this.container = container;
		this.callback = callback;
		this.cache = container.access(IGitLocal.class, Functions.<IGitLocal, IGitLocal> identity()).get();
	}

	@Override
	public Future<?> request(final String groupId, final String groupCryptoKey, final String month) {
		return container.access(IHttpClient.class, new IFunction1<IHttpClient, Future<?>>() {
			@Override
			public Future<?> apply(IHttpClient client) throws Exception {
				return client.post(GroupConstants.generateGroupReportPrefix).//
						addParam(GroupConstants.groupIdKey, groupId).//
						addParam(GroupConstants.groupCryptoKey, groupCryptoKey).//
						addParam(GroupConstants.monthKey, month).//
						execute(new IResponseCallback() {
							@Override
							public void process(IResponse response) {
								cache.clearCaches();
								callback.process(response);
							}
						});
			}
		}).get();
	}
}
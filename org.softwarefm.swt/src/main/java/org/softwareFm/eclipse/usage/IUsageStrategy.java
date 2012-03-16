/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.response.IResponse;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.future.Futures;
import org.softwareFm.common.maps.IHasCache;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.snippets.internal.UsageStrategy;
import org.softwareFm.eclipse.usage.internal.CachedUsageStrategy;
import org.softwareFm.swt.explorer.IUserDataManager;

public interface IUsageStrategy {

	public Future<?> using(String softwareFmId, String digest, IResponseCallback callback);

	public Map<String, Object> myProjectData(String softwareFmId, String crypto);

	public static class Utils {
		public static IUsageStrategy cached(IUsageStrategy delegate, long period, IHasCache cachesToClear, IUserDataManager userDataManager ){
			return new CachedUsageStrategy(delegate, period, cachesToClear, userDataManager);
		}
		

		public static IUsageStrategy usage(final IServiceExecutor serviceExecutor, final IHttpClient client, final IGitLocal gitLocal, IUrlGenerator userGenerator) {
			return new UsageStrategy(client, serviceExecutor, gitLocal, userGenerator);
		}

		public static IUsageStrategy noUsageStrategy() {
			return new IUsageStrategy() {
				@Override
				public Map<String, Object> myProjectData(String softwareFmId, String crypto) {
					throw new UnsupportedOperationException();
				}

				@Override
				public Future<?> using(String softwareFmId, String digest, IResponseCallback callback) {
					throw new UnsupportedOperationException();
				}
			};
		}

		public static IUsageStrategy sysoutUsageStrategy() {
			return new IUsageStrategy() {
				@Override
				public Future<?> using(String softwareFmId, String digest, IResponseCallback callback) {
					String message = "Using: SoftwareFmId: " + softwareFmId + ", Digest: " + digest;
					System.out.println(message);
					callback.process(IResponse.Utils.okText("", message));
					return Futures.doneFuture(null);
				}

				@Override
				public Map<String, Object> myProjectData(String softwareFmId, String crypto) {
					String message = "myProjectData: SoftwareFmId: " + softwareFmId + ", Crypto: " + crypto;
					System.out.println(message);
					Map<String, Object> result = Maps.emptyStringObjectMap();
					return result;
				}
			};
		}

		
	}

}
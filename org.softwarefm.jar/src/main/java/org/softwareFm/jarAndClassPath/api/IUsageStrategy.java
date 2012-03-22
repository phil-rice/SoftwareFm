/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.api;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.crowdsource.api.ICrowdSourceReaderApi;
import org.softwareFm.crowdsource.httpClient.IResponse;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.future.Futures;
import org.softwareFm.crowdsource.utilities.maps.IHasCache;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.jarAndClassPath.internal.CachedUsageStrategy;
import org.softwareFm.jarAndClassPath.internal.UsageStrategy;

public interface IUsageStrategy {

	public Future<?> using(String softwareFmId, String digest, IResponseCallback callback);

	public Map<String, Object> myProjectData(String softwareFmId, String crypto);

	public static class Utils {
		public static IUsageStrategy cached(IUsageStrategy delegate, long period, IHasCache cachesToClear, IUserDataManager userDataManager) {
			return new CachedUsageStrategy(delegate, period, cachesToClear, userDataManager);
		}

		public static IUsageStrategy usage(final IServiceExecutor serviceExecutor, final ICrowdSourceReaderApi readerApi, IUrlGenerator userGenerator) {
			return new UsageStrategy(readerApi, serviceExecutor, userGenerator);
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
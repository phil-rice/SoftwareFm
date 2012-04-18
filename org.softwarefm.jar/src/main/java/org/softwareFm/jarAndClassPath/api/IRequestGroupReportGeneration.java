/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.api;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.future.Futures;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.jarAndClassPath.internal.RequestGroupReportGeneration;

public interface IRequestGroupReportGeneration {

	Future<?> request(String groupId, String groupCryptoKey, String month);

	public static class Utils {
		public static IRequestGroupReportGeneration httpClient(IContainer container, IResponseCallback callback) {
			return new RequestGroupReportGeneration(container, callback);
		}

		public static IRequestGroupReportGeneration withCache(final IRequestGroupReportGeneration reportGeneration, final long period) {
			return new IRequestGroupReportGeneration() {
				private final Map<String, Long> map = Maps.newMap();

				@Override
				public Future<?> request(String groupId, String groupCryptoKey, String month) {
					long now = System.currentTimeMillis();
					Long last = Maps.getOrDefault(map, groupId, now - period * 2);
					if (now > period + last) {
						map.put(groupId, now);
						return reportGeneration.request(groupId, groupCryptoKey, month);
					}
					return Futures.doneFuture(null);
				}
			};
		}
	}

}
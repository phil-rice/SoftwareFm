package org.softwareFm.eclipse;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.common.future.Futures;
import org.softwareFm.common.maps.Maps;

public interface IRequestGroupReportGeneration {

	Future<?> request(String groupId, String groupCryptoKey, String month);

	public static class Utils {
		public static IRequestGroupReportGeneration withCache(final IRequestGroupReportGeneration reportGeneration, final long period) {
			return new IRequestGroupReportGeneration() {
				private final Map<String, Long> map = Maps.newMap();

				@Override
				public Future<?> request(String groupId, String groupCryptoKey, String month) {
					long now = System.currentTimeMillis();
					Long last = Maps.getOrDefault(map, groupId, now - period * 2);
					if (now > period + last){
						System.out.println("requesting: " + groupId);
						map.put(groupId, now);
						return reportGeneration.request(groupId, groupCryptoKey, month);
					}
					System.out.println("no need to request: " + groupId);
					return Futures.doneFuture(null);
				}
			};
		}
	}

}

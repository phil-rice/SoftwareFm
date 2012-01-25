package org.softwareFm.collections.explorer;

import java.util.concurrent.Future;

import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;

public interface IUsageStrategy {

	public Future<?> using(String softwareFmId, String groupId, String artifactId, IResponseCallback callback);

	public static class Utils {
		public static IUsageStrategy usage(final IHttpClient client) {
			return usage(client, CollectionConstants.groupId, CollectionConstants.artifactId);
		}

		public static IUsageStrategy usage(final IHttpClient client, final String groupIdKey, final String artifactIdKey) {
			return new IUsageStrategy() {
				@Override
				public Future<?> using(String softwareFmId, String groupId, String artifactId, IResponseCallback callback) {
					return client.post(ServerConstants.usagePrefix).//
							addParam(ServerConstants.softwareFmIdKey, softwareFmId).//
							addParam(groupIdKey, groupId).//
							addParam(artifactIdKey, artifactId).//
							execute(callback);
				}
			};
		}

		public static IUsageStrategy noUsageStrategy() {
			return new IUsageStrategy() {
				@Override
				public Future<?> using(String softwareFmId, String groupId, String artifactId, IResponseCallback callback) {
					throw new IllegalStateException();
				}
			};
		}
	}

}

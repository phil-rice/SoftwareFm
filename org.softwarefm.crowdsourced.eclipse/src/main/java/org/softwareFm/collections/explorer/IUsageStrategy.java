package org.softwareFm.collections.explorer;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.collections.explorer.internal.UsageStrategy;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.server.IGitLocal;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwareFm.utilities.url.IUrlGenerator;

public interface IUsageStrategy {

	public Future<?> using(String softwareFmId, String groupId, String artifactId, IResponseCallback callback);

	public Map<String, Object> myProjectData(String softwareFmId, String crypto);

	public static class Utils {

		public static IUsageStrategy usage(final IServiceExecutor serviceExecutor, final IHttpClient client, final IGitLocal gitLocal, IUrlGenerator userGenerator, IUrlGenerator projectUrlGenerator) {
			return new UsageStrategy(client, serviceExecutor, gitLocal, userGenerator, projectUrlGenerator);
		}

		public static IUsageStrategy noUsageStrategy() {
			return new IUsageStrategy() {
				@Override
				public Future<?> using(String softwareFmId, String groupId, String artifactId, IResponseCallback callback) {
					throw new UnsupportedOperationException();
				}

				@Override
				public Map<String, Object> myProjectData(String softwareFmId, String crypto) {
					throw new UnsupportedOperationException();
				}

			};
		}

		public static IUsageStrategy sysoutUsageStrategy() {
			return new IUsageStrategy() {
				@Override
				public Future<?> using(String softwareFmId, String groupId, String artifactId, IResponseCallback callback) {
					String message = "Using: SoftwareFmId: " + softwareFmId + ", GroupId: " + groupId + ", ArtifactId: " + artifactId;
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

		public static UsageStrategyMock mockUsageStartegy() {
			return new UsageStrategyMock();
		}
	}

}

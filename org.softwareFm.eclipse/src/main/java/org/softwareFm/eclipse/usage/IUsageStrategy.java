package org.softwareFm.eclipse.usage;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.response.IResponse;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.future.Futures;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.snippets.internal.UsageStrategy;

public interface IUsageStrategy {

	public Future<?> using(String softwareFmId, String groupId, String artifactId, IResponseCallback callback);

	public Map<String, Object> myProjectData(String softwareFmId, String crypto);

	public static class Utils {

		public static IUsageStrategy usage(final IServiceExecutor serviceExecutor, final IHttpClient client, final IGitLocal gitLocal, IUrlGenerator userGenerator) {
			return new UsageStrategy(client, serviceExecutor, gitLocal, userGenerator);
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

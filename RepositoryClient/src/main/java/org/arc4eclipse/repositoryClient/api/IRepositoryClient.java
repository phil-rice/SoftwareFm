package org.arc4eclipse.repositoryClient.api;

import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.constants.HttpClientConstants;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.repositoryClient.api.impl.RepositoryClient;
import org.arc4eclipse.repositoryClient.paths.IPathCalculator;

public interface IRepositoryClient<Thing, Aspect> {

	void getDetails(Thing thing, Aspect aspect, IResponseCallback<Thing, Aspect> callback);

	void setDetails(Thing thing, Aspect aspect, IResponseCallback<Thing, Aspect> callback, String... details);

	static class Utils {
		public static <Thing, Aspect> IRepositoryClient<Thing, Aspect> repositoryClient(IPathCalculator<Thing, Aspect> pathCalculator, IHttpClient client) {
			return new RepositoryClient<Thing, Aspect>(pathCalculator, client);
		}

		public static IRepositoryClient<Object, IEntityType> repositoryClientForClassAndMethod(IJarDetails jarDetails) {
			return repositoryClientForClassAndMethod(jarDetails, HttpClientConstants.defaultHost, HttpClientConstants.defaultPort, HttpClientConstants.userName, HttpClientConstants.password);
		}

		public static IRepositoryClient<Object, IEntityType> repositoryClientForClassAndMethod(IJarDetails jarDetails, String host, int port) {
			return repositoryClientForClassAndMethod(jarDetails, host, port, HttpClientConstants.userName, HttpClientConstants.password);
		}

		public static IRepositoryClient<Object, IEntityType> repositoryClientForClassAndMethod(IJarDetails jarDetails, String host, int port, String userName, String password) {
			IPathCalculator<Object, IEntityType> classPathCalculator = IPathCalculator.Utils.classPathCalculator(jarDetails);
			IHttpClient client = IHttpClient.Utils.builder(host, port).withCredentials(userName, password);
			return repositoryClient(classPathCalculator, client);
		}

	}

}

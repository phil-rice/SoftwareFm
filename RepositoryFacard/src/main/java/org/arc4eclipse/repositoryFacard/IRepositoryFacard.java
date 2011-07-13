package org.arc4eclipse.repositoryFacard;

import java.util.Map;

import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.repositoryClient.api.IRepositoryClient;
import org.arc4eclipse.repositoryClient.paths.IPathCalculator;
import org.arc4eclipse.repositoryFacard.impl.AspectToParameters;
import org.arc4eclipse.repositoryFacard.impl.AspectToParametersAsDataValue;
import org.arc4eclipse.repositoryFacard.impl.RepositoryFacard;

public interface IRepositoryFacard<Key, Thing, Aspect, Data> {
	void getDetails(Key key, Thing thing, Aspect aspect, IRepositoryFacardCallback<Key, Thing, Aspect, Data> callback);

	void setDetails(Key key, Thing thing, Aspect aspect, Data data, IRepositoryFacardCallback<Key, Thing, Aspect, Data> callback);

	public static class Utils {

		public static <Key, Thing, Aspect> IRepositoryFacard<Key, Thing, Aspect, Map<Object, Object>> facardUsingDataField(IRepositoryClient<Thing, Aspect> repositoryClient) {
			return new RepositoryFacard<Key, Thing, Aspect, Map<Object, Object>>(repositoryClient, new AspectToParametersAsDataValue<Thing, Aspect>());
		}

		public static <Key, Thing, Aspect> IRepositoryFacard<Key, Thing, Aspect, Map<Object, Object>> facard(IRepositoryClient<Thing, Aspect> repositoryClient) {
			return new RepositoryFacard<Key, Thing, Aspect, Map<Object, Object>>(repositoryClient, new AspectToParameters<Thing, Aspect>());
		}

		public static <Key, Thing, Aspect, Data> IRepositoryFacard<Key, Thing, Aspect, Data> facard(IRepositoryClient<Thing, Aspect> repositoryClient, IAspectToParameters<Thing, Aspect, Data> aspectToParameters) {
			return new RepositoryFacard<Key, Thing, Aspect, Data>(repositoryClient, aspectToParameters);
		}

		public static <Key, Thing, Aspect, Data> IRepositoryFacard<Key, Thing, Aspect, Data> facard(IAspectToParameters<Thing, Aspect, Data> aspectToParameters, String hostName, int port, String userName, String password, IPathCalculator<Thing, Aspect> pathCalculator) {
			IHttpClient httpClient = IHttpClient.Utils.builder(hostName, port).withCredentials(userName, password);
			IRepositoryClient<Thing, Aspect> repositoryClient = IRepositoryClient.Utils.repositoryClient(pathCalculator, httpClient);
			return new RepositoryFacard<Key, Thing, Aspect, Data>(repositoryClient, aspectToParameters);
		}

	}

}

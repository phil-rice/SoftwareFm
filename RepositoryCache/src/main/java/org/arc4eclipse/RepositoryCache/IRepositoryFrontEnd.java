package org.arc4eclipse.RepositoryCache;

import java.util.Map;

import org.arc4eclipse.RepositoryCache.impl.RepositoryFrontEnd;
import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.utilities.callbacks.ICallback;

public interface IRepositoryFrontEnd {

	/** This will delete the node at the Url */
	void delete(String url, IResponseCallback callback);

	/** This will call the callback with the .json representation of the node at the Url */
	void get(String url, ICallback<Map<String, Object>> callback);

	/** This will call the callback with the .<depth>.json representation of the node at the Url */
	void getDepth(String url, int depth, ICallback<Map<String, Object>> callback);

	/** This will create or update the node at the location. The map <em>must</em> be a map from string to: Integer,Long,String,String[] */
	void post(String url, Map<String, Object> map, IResponseCallback callback);

	/**
	 * This creates one or more nodes underneath the parentNode<br />
	 * assume parentUrl='content',<br />
	 * map = {'sample', {'propOne':'propOneValue, 'childOne':{'childPropOne': true}}}' would make content/sample and content/sample/childOne
	 */
	void postMany(String parentUrl, Map<String, Object> map, IResponseCallback callback);

	public static class Utils {

		public static IRepositoryFrontEnd defaultFrontEnd() {
			return new RepositoryFrontEnd(IHttpClient.Utils.defaultClient());
		}

		public static IRepositoryFrontEnd frontEnd(String host, int port, String userName, String password) {
			return new RepositoryFrontEnd(IHttpClient.Utils.builder(host, port).withCredentials(userName, password));
		}

	}

}

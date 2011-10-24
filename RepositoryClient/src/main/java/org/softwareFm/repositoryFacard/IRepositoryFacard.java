package org.softwareFm.repositoryFacard;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.impl.RepositoryFacard;

public interface IRepositoryFacard {

	/** adds this as an Http header to all requests */
	void addHeader(String name, String value);

	/** This will delete the node at the Url */
	Future<?> delete(String url, IResponseCallback callback);

	/** This will call the callback with the .json representation of the node at the Url */
	Future<?> get(String url, IRepositoryFacardCallback callback);

	/** This will call the callback with the .<depth>.json representation of the node at the Url */
	Future<?> getDepth(String url, int depth, IRepositoryFacardCallback callback);

	/** This will create or update the node at the location. The map <em>must</em> be a map from string to: Integer,Long,String,String[] */
	Future<?> post(String url, Map<String, Object> map, IResponseCallback callback);

	/**
	 * This creates one or more nodes underneath the parentNode<br />
	 * assume parentUrl='content',<br />
	 * map = {'sample', {'propOne':'propOneValue, 'childOne':{'childPropOne': true}}}' would make content/sample and content/sample/childOne
	 */
	Future<?> postMany(String parentUrl, Map<String, Object> map, IResponseCallback callback);

	void shutdown();

	public static class Utils {

		public static IRepositoryFacard defaultFacard() {
			return new RepositoryFacard(IHttpClient.Utils.defaultClient(), "sfm");
		}

		public static IRepositoryFacard defaultFacardForCardExplorer() {
			return new RepositoryFacard(IHttpClient.Utils.defaultClient(), "1.json");
		}

		public static IRepositoryFacard defaultFacardWithHeaders(String name, String value) {
			return new RepositoryFacard(IHttpClient.Utils.defaultClient().setDefaultHeaders(Arrays.<NameValuePair> asList(new BasicNameValuePair(name, value))), "sfm");
		}

		public static IRepositoryFacard frontEnd(String host, int port, String userName, String password) {
			return new RepositoryFacard(IHttpClient.Utils.builder(host, port).withCredentials(userName, password), "sfm");
		}

	}

}

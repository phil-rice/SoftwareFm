package org.softwarefm.httpServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.softwarefm.httpServer.internal.HttpServer;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.HttpMethod;

public interface IHttpServer extends IHttpRegistry {

	void start();

	void shutdown();

	void configure(IRegistryConfigurator configurator);

	public static class Utils {

		public static List<NameValuePair> makeList(String... parameters) {
			List<NameValuePair> result = new ArrayList<NameValuePair>();
			assert parameters.length % 2 == 0 : parameters;
			for (int i = 0; i < parameters.length; i += 2)
				result.add(new BasicNameValuePair(parameters[i + 0], parameters[i + 1]));
			return result;
		}

		public static StatusAndEntity process(IHttpServer server, HttpMethod method, String uri, String... parameters) {
			try {
				String paramsString = URLEncodedUtils.format(makeList(parameters), "UTF-8");
				StatusAndEntity result = server.process(method, uri, new StringEntity(paramsString));
				return result;
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		public static HttpParams simpleParams(String serviceText) {
			HttpParams params = new SyncBasicHttpParams()//
					.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 2000)//
					.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)//
					.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)//
					.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)//
					.setParameter(CoreProtocolPNames.ORIGIN_SERVER, "SoftwareFmUsageServer/1.0");
			return params;
		}

		public static HttpRequestHandler helloWorldHandler() {
			return new HttpRequestHandler() {
				private final AtomicLong count = new AtomicLong();

				public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
					long thisCount = count.incrementAndGet();
					System.out.println("In request handler" + thisCount + " in thread: " + Thread.currentThread());
					response.setEntity(new StringEntity("Hello everyone " + thisCount));

				}
			};
		}

		public static IHttpServer server(int noOfThreads, HttpParams httpParams, int port, ICallback<Throwable> exceptionHandler) {
			return new HttpServer(noOfThreads, httpParams, port, exceptionHandler);
		}

		public static Map<String, String> getParameters(HttpRequest request) throws HttpException {
			try {
				if (request instanceof HttpEntityEnclosingRequest) {
					List<NameValuePair> list = URLEncodedUtils.parse(((HttpEntityEnclosingRequest) request).getEntity());
					Map<String, String> result = new HashMap<String, String>();
					for (NameValuePair pair : list)
						if (result.containsKey(pair.getName()))
							throw new HttpException("Duplicate parameter: " + pair.getName());
						else
							result.put(pair.getName(), pair.getValue());
					return result;
				}
			} catch (IOException e) {
				throw new HttpException("Cannot decode parameters", e);
			}
			throw new HttpException("Cannot get parameters as request is of type " + request.getClass().getName());
		}

	}

}
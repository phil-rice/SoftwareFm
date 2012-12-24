package org.softwarefm.utilities.http;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.http.internal.HttpServer;

public interface IHttpServer {

	void start();

	void shutdown();

	public static class Utils {

		public static HttpParams simpleParams(String serviceText) {
			HttpParams params = new SyncBasicHttpParams()//
					.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)//
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

		public static IHttpServer server(int noOfThreads, HttpParams httpParams, int port, HttpRequestHandler handler, ICallback<Throwable> exceptionHandler) {
			return new HttpServer(noOfThreads, httpParams, port, handler, exceptionHandler);
		}

	}

}
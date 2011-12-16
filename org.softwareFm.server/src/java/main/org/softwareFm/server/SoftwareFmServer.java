package org.softwareFm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.RequestLine;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.services.IServiceExecutor;

public class SoftwareFmServer {

	public SoftwareFmServer(int port, int threads, final IProcessCall processCall, final ICallback<Throwable> errorHandler) throws Exception {
		final ServerSocket serverSocket = new ServerSocket(port);
		IServiceExecutor executor = IServiceExecutor.Utils.defaultExecutor();
		for (int i = 0; i < threads; i++) {
			executor.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					while (true)
						try {
							Socket socket = serverSocket.accept();
							HttpParams params = new BasicHttpParams();
							DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
							conn.bind(socket, params);
							HttpRequest request = conn.receiveRequestHeader();
							Map<String, Object> value = getValue(conn, request);
							RequestLine requestLine = request.getRequestLine();
							String replyString = processCall.process(requestLine, value);
							HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
							response.setEntity(new StringEntity(replyString));
							conn.sendResponseHeader(response);
							conn.sendResponseEntity(response);
							conn.close();
							System.out.println("Turned: " + value + " into " + replyString);
							return null;
						} catch (ThreadDeath e) {
							throw e;
						} catch (Throwable t) {
							errorHandler.process(t);
						}
				}

				private Map<String, Object> getValue(DefaultHttpServerConnection conn, HttpRequest request) throws HttpException, IOException {
					if (request instanceof HttpEntityEnclosingRequest) {
						conn.receiveRequestEntity((HttpEntityEnclosingRequest) request);
						// Do something useful with the entity and, when done, ensure all
						// content has been consumed, so that the underlying connection
						// coult be re-used
						HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
						if (entity != null) {
							List<NameValuePair> parse = URLEncodedUtils.parse(entity);
							EntityUtils.consume(entity);
							Map<String, Object> result = Maps.newMap();
							for (NameValuePair pair : parse)
								result.put(pair.getName(), pair.getValue());
							return result;
						}
					}
					return Collections.emptyMap();
				}
			});
		}
	}

	public static void main(String[] args) throws Exception {
		new SoftwareFmServer(8080, 10, new IProcessCall() {
			@Override
			public String process(RequestLine requestLine, Map<String, Object> parameters) {
				System.out.println("uri: " + requestLine.getUri());
				return ("<" + requestLine + ", " + parameters + ">");
			}
		}, ICallback.Utils.sysErrCallback());
	}
}

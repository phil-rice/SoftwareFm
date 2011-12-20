package org.softwareFm.server.internal;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.services.IServiceExecutor;

public class SoftwareFmServer implements ISoftwareFmServer {

	private IServiceExecutor executor;
	private boolean shutdown;
	private CountDownLatch countDownLatch;
	private ServerSocket serverSocket;

	public SoftwareFmServer(int port, int threads, final IProcessCall processCall, final ICallback<Throwable> errorHandler) {
		try {
			serverSocket = new ServerSocket(port);
			executor = IServiceExecutor.Utils.defaultExecutor(threads);
			countDownLatch = new CountDownLatch(threads);
			for (int i = 0; i < threads; i++) {
				executor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						try {
							while (!shutdown)
								try {
									Socket socket = serverSocket.accept();
									HttpParams params = new BasicHttpParams();
									DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
									try {
										conn.bind(socket, params);
										HttpRequest request = conn.receiveRequestHeader();
										Map<String, Object> value = getValue(conn, request);
										RequestLine requestLine = request.getRequestLine();
										HttpResponse response = process(processCall, value, requestLine);
										conn.sendResponseHeader(response);
										conn.sendResponseEntity(response);
									} finally {
										conn.close();
									}
								} catch (ThreadDeath e) {
									throw e;
								} catch (Throwable t) {
									errorHandler.process(t);

								}
						} finally {
							countDownLatch.countDown();
						}
						return null;
					}

					private HttpResponse process(final IProcessCall processCall, Map<String, Object> value, RequestLine requestLine) throws UnsupportedEncodingException {
						try {
							String replyString = processCall.process(requestLine, value);
							return makeResponse(replyString, 200, "OK");
						} catch (Exception e) {
							return makeResponse(e.getClass() + "/" + e.getMessage(), 500, e.getMessage());
						}
					}

					private HttpResponse makeResponse(String replyString, int status, String reason) throws UnsupportedEncodingException {
						HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, status, "OK");
						response.setEntity(new StringEntity(replyString));
						return response;
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
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void shutdown() {
		try {
			shutdown = true;
			executor.shutdown();// stop accepting new queries... aha that means the threads that are
			countDownLatch.await(10, TimeUnit.MILLISECONDS); // this gives any fast actions time to finish
			try {
				serverSocket.close();
			} catch (IOException e) {
			}
			int tries = 0;
			while (tries++ < 1000 && !countDownLatch.await(10, TimeUnit.MILLISECONDS))
				;
			if (tries >= 1000)
				throw new RuntimeException(ServerConstants.couldntStop);
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				throw WrappedException.wrap(e);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		File root = new File(System.getProperty("user.home"));
		IGitServer server = IGitServer.Utils.gitServer(new File(root, ".sfm_remote"), "not used");
		new SoftwareFmServer(8080, 10, IProcessCall.Utils.softwareFmProcessCall(server), ICallback.Utils.sysErrCallback());
	}
}

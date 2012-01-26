package org.softwareFm.server.internal;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.RequestLine;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.IUsage;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.IMailer;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.user.internal.UserCryptoFn;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.runnable.Callables;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwareFm.utilities.strings.Strings;
import org.softwareFm.utilities.url.IUrlGenerator;

public class SoftwareFmServer implements ISoftwareFmServer {

	private IServiceExecutor executor;
	private boolean shutdown;
	private CountDownLatch shutdownLatch;
	private ServerSocket serverSocket;
	private final IUsage usage;

	public SoftwareFmServer(int port, int threads, final IProcessCall processCall, final ICallback<Throwable> errorHandler, final IUsage usage) {
		this.usage = usage;
		usage.start();
		final CountDownLatch startLatch = new CountDownLatch(threads);
		try {
			serverSocket = new ServerSocket(port);
			executor = IServiceExecutor.Utils.executor(threads);
			shutdownLatch = new CountDownLatch(threads);
			Callable<Void> callable = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					long aggregatedUsage = 0;
					long count = 0;
					startLatch.countDown();
					while (!shutdown)
						try {
							try {
								Socket socket = serverSocket.accept();
								long startTime = System.nanoTime();
								HttpParams params = new BasicHttpParams();
								DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
								String ip = null;
								String requestLineAsString = null;
								String prefix = "";
								try {
									conn.bind(socket, params);
									HttpRequest request = conn.receiveRequestHeader();
									Map<String, Object> value = getValue(conn, request);
									RequestLine requestLine = request.getRequestLine();
									HttpResponse response = process(processCall, value, requestLine);
									prefix += socket.getRemoteSocketAddress() + ", " + requestLine + " " + response.getStatusLine();
									conn.sendResponseHeader(response);
									conn.sendResponseEntity(response);

									requestLineAsString = requestLine.toString();
									String remoteSocketAddress = socket.getRemoteSocketAddress().toString();
									ip = Strings.firstSegment(remoteSocketAddress, ":");
								} finally {
									conn.close();
									long now = System.nanoTime();
									long duration = now - startTime;
									long nowms = System.currentTimeMillis();

									usage.monitor(ip, requestLineAsString, duration);

									long usageDuration = System.nanoTime() - now;
									long usageMs = System.currentTimeMillis() - nowms;

									aggregatedUsage += usageDuration;
									count += 1;
									double nanosToMilli = 1E6;
									System.out.println(String.format(prefix + "...took %10.2fms Usage %10.2fms AvgUsage %10.2f Usagems %10.2f", duration / nanosToMilli, usageDuration / nanosToMilli, aggregatedUsage / count / nanosToMilli, usageMs * 1.0));

								}
							} catch (ThreadDeath e) {
								throw e;
							} catch (Throwable t) {
								if (!shutdown)
									errorHandler.process(t);
							}
						} finally {
							shutdownLatch.countDown();
						}
					return null;
				}

				private HttpResponse process(final IProcessCall processCall, Map<String, Object> value, RequestLine requestLine) throws Exception {
					try {
						IProcessResult processResult = processCall.process(requestLine, value);
						return makeResponse(processResult, 200, "OK");
					} catch (Exception e) {
						return makeResponse(IProcessResult.Utils.processString(e.getClass() + "/" + e.getMessage()), 500, e.getMessage());
					}
				}

				private HttpResponse makeResponse(IProcessResult processResult, int status, String reason) throws Exception {
					HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, status, "OK");
					processResult.process(response);
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
			};
			for (int i = 0; i < threads; i++) {
				executor.submit(callable);
			}
			startLatch.await();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
		System.out.println("Started with " + threads + " threads");
	}

	@Override
	public void shutdown() {
		try {
			shutdown = true;
			usage.shutdown();
			executor.shutdown();// stop accepting new queries... aha that means the threads that are
			shutdownLatch.await(10, TimeUnit.MILLISECONDS); // this gives any fast actions time to finish
			try {
				serverSocket.close();
			} catch (IOException e) {
			}
			int tries = 0;
			while (tries++ < 1000 && !shutdownLatch.await(10, TimeUnit.MILLISECONDS))
				doNothing();
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

	private void doNothing() {
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		File root = new File(System.getProperty("user.home"));
		File sfmRoot = new File(root, ".sfm_remote");
		IGitServer server = IGitServer.Utils.gitServer(sfmRoot, "not used");
		System.out.println("Server: " + server);
		final IUsage usage = IUsage.Utils.defaultUsage();
		IMailer mailer = IMailer.Utils.email("localhost", null, null);
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		IFunction1<Map<String, Object>, String> cryptoFn = new UserCryptoFn(dataSource);
		Callable<String> monthGetter = Callables.monthGetter();
		Callable<Integer> dayGetter = Callables.dayGetter();
		Callable<String> softwareFmIdGenerator = Callables.uuidGenerator();
		Callable<String> makeKey = Callables.makeCryptoKey();
		IUrlGenerator userGenerator = ServerConstants.userGenerator();
		IUrlGenerator projectGenerator = ServerConstants.projectGenerator();
		new SoftwareFmServer(8080, 1000, IProcessCall.Utils.softwareFmProcessCall(dataSource, server, cryptoFn, makeKey, sfmRoot, mailer, monthGetter, dayGetter, softwareFmIdGenerator), ICallback.Utils.sysErrCallback(), usage);
	}
}

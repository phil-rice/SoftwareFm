/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.constants.CommonMessages;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.IUsage;
import org.softwareFm.server.processors.IMailer;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ProcessCallParameters;

public class CrowdSourcedServer implements ICrowdSourcedServer {

	private IServiceExecutor executor;
	private boolean shutdown;
	private CountDownLatch shutdownLatch;
	private ServerSocket serverSocket;
	private final IUsage usage;

	public CrowdSourcedServer(int port, int threads, final IProcessCall processCall, final ICallback<Throwable> errorHandler, final IUsage usage) {
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
								long startTime = System.currentTimeMillis();
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
									long now = System.currentTimeMillis();
									long duration = now - startTime;

									usage.monitor(ip, requestLineAsString, duration);

									long usageDuration = System.currentTimeMillis() - now;

									aggregatedUsage += usageDuration;
									count += 1;
									double nanosToMilli = 1E6;
									System.out.println(String.format(prefix + "...took %10.2fms Usage %10.2fms AvgUsage %10.2f", duration / nanosToMilli, usageDuration / nanosToMilli, aggregatedUsage / count / nanosToMilli));

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
						e.printStackTrace();
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
				throw new RuntimeException(CommonMessages.couldntStop);
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



	public static void main(String[] args) throws Exception {
		File sfmRoot = Utils.makeSfmRoot();
		IGitOperations gitOperations = IGitOperations.Utils.gitOperations(sfmRoot);
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		makeServer(Utils.port(args), gitOperations, dataSource, Functions.<ProcessCallParameters, IProcessCall[]> constant(new IProcessCall[0]), "softwarefm");
	}

	public static ICrowdSourcedServer makeServer(int port, IGitOperations gitOperations, BasicDataSource dataSource, IFunction1<ProcessCallParameters, IProcessCall[]> extraProcessCalls, String prefix) {
		System.out.println("Server: " + gitOperations);
		final IUsage usage = IUsage.Utils.defaultUsage();
		IMailer mailer = IMailer.Utils.email("localhost", null, null);
		Callable<String> softwareFmIdGenerator = Callables.uuidGenerator();
		IFunction1<Map<String, Object>, String> userCryptoFn = new UserCryptoFn(dataSource);
		Map<String, Callable<Object>> defaultValue = Maps.newMap();
		ProcessCallParameters processCallParameters = new ProcessCallParameters(dataSource, gitOperations, Callables.makeCryptoKey(), softwareFmIdGenerator, userCryptoFn, mailer, defaultValue, prefix);
		IProcessCall processCall = IProcessCall.Utils.softwareFmProcessCall(processCallParameters, extraProcessCalls);
		return new CrowdSourcedServer(port, 1000, processCall, ICallback.Utils.sysErrCallback(), usage);
	}
}
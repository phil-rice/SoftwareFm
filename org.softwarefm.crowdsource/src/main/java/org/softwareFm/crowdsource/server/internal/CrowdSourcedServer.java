/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.internal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.server.IUsage;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class CrowdSourcedServer implements ICrowdSourcedServer {

	private boolean shutdown;
	private ServerSocket serverSocket;
	private final IUsage usage;
	private Thread socketListeningThread;

	public CrowdSourcedServer(final int port, final IContainer container, final ICallback<Throwable> errorHandler, final IUsage usage) throws IOException {
		this.usage = usage;
		ICrowdSourcedServer.logger.debug(MessageFormat.format("Starting server on port {0} ", port));
		this.serverSocket = new ServerSocket(port);

		socketListeningThread = new Thread() {
			@Override
			public void run() {
				usage.start();
				try {
					while (!shutdown) {
						final Socket socket = serverSocket.accept();
						final AtomicLong aggregatedUsage = new AtomicLong();
						final AtomicLong count = new AtomicLong();
						container.access(ICallProcessor.class, new ICallback<ICallProcessor>() {
							@Override
							public void process(ICallProcessor callProcessor) throws Exception {
								try {
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
										HttpResponse response = process(callProcessor, value, requestLine);
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
										double nanosToMilli = 1E6;
										ICrowdSourcedServer.logger.debug(String.format(prefix + "...took %10.2fms Usage %10.2fms AvgUsage %10.2f", duration / nanosToMilli, usageDuration / nanosToMilli, aggregatedUsage.addAndGet(usageDuration) / count.incrementAndGet() / nanosToMilli));
									}
								} catch (Exception e) {
									ICrowdSourcedServer.logger.error("Server main method", e);
									errorHandler.process(e);
								}
							}

							private Map<String, Object> getValue(DefaultHttpServerConnection conn, HttpRequest request) throws HttpException, IOException {
								if (request instanceof HttpEntityEnclosingRequest) {
									conn.receiveRequestEntity((HttpEntityEnclosingRequest) request);
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

							private HttpResponse process(final ICallProcessor callProcessor, Map<String, Object> value, RequestLine requestLine) throws Exception {
								try {
									IProcessResult processResult = callProcessor.process(requestLine, value);
									return makeResponse(processResult, 200, "OK");
								} catch (Exception e) {
									e.printStackTrace();
									ICrowdSourcedServer.logger.error("Calling the call processor", e);
									return makeResponse(IProcessResult.Utils.processString(e.getClass() + "/" + e.getMessage()), 500, e.getMessage());
								}
							}

							private HttpResponse makeResponse(IProcessResult processResult, int status, String reason) throws Exception {
								HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, status, "OK");
								processResult.process(response);
								return response;
							}

						});

					}
				} catch (Exception e) {
					if (!shutdown)
						throw WrappedException.wrap(e);
				}
			}
		};
		socketListeningThread.setName("SocketListeningThread");
		socketListeningThread.start();
	}

	@Override
	public void shutdown() {
		try {
			shutdown = true;
			serverSocket.close();
			usage.shutdown();
		} catch (IOException e) {
			ICrowdSourcedServer.logger.error("Shutdown", e);
			throw WrappedException.wrap(e);
		}
	}

}
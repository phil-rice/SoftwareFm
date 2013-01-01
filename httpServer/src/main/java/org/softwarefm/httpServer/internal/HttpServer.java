package org.softwarefm.httpServer.internal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.softwarefm.httpServer.IHttpRegistry;
import org.softwarefm.httpServer.IHttpServer;
import org.softwarefm.httpServer.IRegistryConfigurator;
import org.softwarefm.httpServer.StatusAndEntity;
import org.softwarefm.httpServer.routes.IRouteHandler;
import org.softwarefm.httpServer.routes.internal.RouteRequestHandler;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.HttpMethod;

public class HttpServer implements IHttpServer {

	private final ThreadPoolExecutor service;
	private Thread acceptThread;
	private final int port;
	private final HttpService httpService;
	private final ICallback<Throwable> exceptionHandler;
	private final AtomicLong count = new AtomicLong();
	protected boolean cont = true;
	private final HttpParams httpParams;
	private ServerSocket serverSocket;
	private final RouteRequestHandler routeRequestHandler;
	private final IHttpRegistry registry;

	public HttpServer(int noOfThreads, HttpParams httpParams, int port, final ICallback<Throwable> exceptionHandler) {
		this.httpParams = httpParams;
		this.port = port;
		this.exceptionHandler = exceptionHandler;
		service = new ThreadPoolExecutor(noOfThreads / 3, noOfThreads, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
		service.setThreadFactory(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
		HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[] { new ResponseDate(), new ResponseServer(), new ResponseContent(), new ResponseConnControl() });
		registry = IHttpRegistry.Utils.registry();
		routeRequestHandler = new RouteRequestHandler(registry);
		HttpRequestHandlerRegistry oldRegistry = new HttpRequestHandlerRegistry();
		oldRegistry.register("*", routeRequestHandler);
		httpService = new HttpService(httpproc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory(), oldRegistry, httpParams);
	}

	@Override
	public void register(HttpMethod method, IRouteHandler routeHandler, String routePattern, String... params) {
		registry.register(method, routeHandler, routePattern, params);
	}

	@Override
	public StatusAndEntity process(HttpMethod method, String uri, HttpEntity entity) {
		try {
			return registry.process(method, uri, entity);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void configure(IRegistryConfigurator configurator) {
		configurator.registerWith(this);
	}

	public void start() {
		try {
			final CountDownLatch latch = new CountDownLatch(1);
			acceptThread = new Thread() {

				@Override
				public void run() {
					try {
						serverSocket = new ServerSocket(port);
						latch.countDown();
						while (cont) {
							final Socket socket = serverSocket.accept();
							service.submit(new Callable<Void>() {
								public Void call() throws Exception {
									HttpContext context = new BasicHttpContext(null);
									DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
									try {
										conn.bind(socket, httpParams);
										while (conn.isOpen())
											httpService.handleRequest(conn, context);
									} catch (SocketTimeoutException e) {
										// TODO I think it is normal for this to happen. But I don't know enough about http1.1 to be sure
									} catch (Exception e) {
										e.printStackTrace();
										throw WrappedException.wrap(e);
									} finally {
										conn.close();
									}
									return null;
								}
							});
						}
					} catch (Throwable e) {
						if (cont) {
							ICallback.Utils.call(exceptionHandler, e);
							throw WrappedException.wrap(e);
						}
					}
					System.out.println("Accept thread closed down");
				}
			};
			acceptThread.setDaemon(true);
			acceptThread.setName("HttpServer accept thread");
			acceptThread.start();
			latch.await();
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		}
	}

	public void shutdown() {
		try {
			cont = false;
			service.shutdown();
			if (acceptThread != null)
				acceptThread.interrupt();
			if (serverSocket != null)
				serverSocket.close();
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 80;
		HttpServer httpServer = (HttpServer) IHttpServer.Utils.server(10, IHttpServer.Utils.simpleParams("Hello World"), port, ICallback.Utils.sysErrCallback());
		httpServer.start();
		while (true) {
			Thread.sleep(20000);
			System.out.println(httpServer.stats());

		}
	}

	private String stats() {
		return "Called: " + count;
	}
}

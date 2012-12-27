package org.softwarefm.utilities.http.internal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpException;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.IHttpServer;

public class HttpServer implements IHttpServer {

	private final ExecutorService service;
	private Thread acceptThread;
	private final int port;
	private final HttpService httpService;
	private final ICallback<Throwable> exceptionHandler;
	private final AtomicLong count = new AtomicLong();
	protected boolean cont = true;
	private final HttpParams httpParams;
	private ServerSocket serverSocket;
	private final HttpRequestHandlerRegistry reqistry;

	public HttpServer(int noOfThreads, HttpParams httpParams, int port, final ICallback<Throwable> exceptionHandler) {
		this.httpParams = httpParams;
		this.port = port;
		this.exceptionHandler = exceptionHandler;
		service = new ThreadPoolExecutor(noOfThreads / 3, noOfThreads, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
		HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[] { new ResponseDate(), new ResponseServer(), new ResponseContent(), new ResponseConnControl() });
		reqistry = new HttpRequestHandlerRegistry();
		httpService = new HttpService(httpproc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory(), reqistry, httpParams) {
			@Override
			public void handleRequest(HttpServerConnection conn, HttpContext context) throws IOException, HttpException {
				try {
					super.handleRequest(conn, context);
				} catch (Exception e) {
					// Shouldn't get here...the handlers are supposed to throw HttpExceptions
					ICallback.Utils.call(exceptionHandler, e);
					conn.close();
				}
			}
		};
	}

	public void register(String path, HttpRequestHandler handler) {
		reqistry.register(path, handler);
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
									conn.bind(socket, httpParams);
									while (conn.isOpen())
										httpService.handleRequest(conn, context);
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
				}
			};
			acceptThread.setName("HttpServer accept thread");
			acceptThread.start();
			latch.await();
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softwarefm.eclipse.usage.IUsageServer#shutdown()
	 */
	public void shutdown() {
		try {
			cont = false;
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

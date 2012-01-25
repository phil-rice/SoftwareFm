package org.softwareFm.server.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.services.IServiceExecutor;

public class GitProxy {
	private final String marker = "git-upload-pack";

	private final IServiceExecutor executor;
	private final IServiceExecutor copyExecutor;
	private boolean shutdown;
	private final ServerSocket serverSocket;
	private final String host;
	private final int toPort;
	private final int timeout;

	private final int simulataneous;

	public GitProxy(int fromPort, final String host, final int toPort, int timeout, int simulataneous) {
		this.simulataneous = simulataneous;
		try {
			this.timeout = timeout;
			this.serverSocket = new ServerSocket(fromPort);
			this.host = host;
			this.toPort = toPort;
			executor = IServiceExecutor.Utils.executor(simulataneous);
			copyExecutor = IServiceExecutor.Utils.executor(simulataneous * 2);

		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	private void listen() {
		for (int i = 0; i < simulataneous; i++) {
			executor.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					while (!shutdown)
						try {
							Socket fromSource = serverSocket.accept();
							CountDownLatch latch = new CountDownLatch(2);
							Socket toDestination = new Socket(host, toPort);
							copyFromToThenClose("From -> To", latch, fromSource.getInputStream(), toDestination.getOutputStream());
							copyFromToThenClose("To -> From", latch, toDestination.getInputStream(), fromSource.getOutputStream());
							latch.await();
							fromSource.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					return null;
				}
			});
		}
	}

	public void shutdown() {
		copyExecutor.shutdownAndAwaitTermination(timeout + 200, TimeUnit.MILLISECONDS);
		shutdown = true;
	}

	public void copyFromToThenClose(final String messagePrefix, final CountDownLatch latch, final InputStream from, final OutputStream to) {
		copyExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					byte[] buffer = new byte[1024];
					while (true) {
						int read = from.read(buffer);
						if (read < 0)
							return null;
						String line = new String(buffer, 0, read, "UTF-8");
						if (line.length() > 4 && line.substring(4).startsWith(marker))
							System.out.println(line);
						to.write(buffer, 0, read);
					}
				} finally {
					latch.countDown();
				}
			}
		});
	}

	public static void main(String[] args) throws Exception {
		new GitProxy(7777, "git.softwarefm.com", 80, 100, 20).listen();

	}

}

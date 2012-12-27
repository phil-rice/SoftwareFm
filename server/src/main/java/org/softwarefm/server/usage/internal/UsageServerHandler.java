package org.softwarefm.server.usage.internal;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.softwarefm.eclipse.usage.IUsage;
import org.softwarefm.eclipse.usage.IUsagePersistance;
import org.softwarefm.eclipse.usage.IUsageStats;
import org.softwarefm.server.usage.IUsageCallback;
import org.softwarefm.server.usage.IUsageServer;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.strings.Strings;

public class UsageServerHandler implements HttpRequestHandler {

	private final IMultipleListenerList dummyListenerList = IMultipleListenerList.Utils.defaultList();
	private final IUsagePersistance persistance;
	private final IUsageCallback callback;

	public UsageServerHandler(IUsagePersistance persistance, IUsageCallback callback) {
		this.persistance = persistance;
		this.callback = callback;
	}

	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		RequestLine requestLine = request.getRequestLine();
		String method = requestLine.getMethod();
		if ("POST".equalsIgnoreCase(method) && request instanceof BasicHttpEntityEnclosingRequest) {
			HttpEntity httpEntity = ((BasicHttpEntityEnclosingRequest) request).getEntity();
			byte[] byteArray = EntityUtils.toByteArray(httpEntity);
			String text = Strings.unzip(byteArray);
			String user = Strings.head(text, "\n");
			String usageText = Strings.tail(text, "\n");
			IUsageStats stats = persistance.parse(usageText);
			callback.process("", user, stats);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		IUsageServer.Utils.usageServer(80, ICallback.Utils.<IUsage> sysoutCallback(), ICallback.Utils.sysErrCallback()).start();
		while (true)
			Thread.sleep(10000);
	}
}

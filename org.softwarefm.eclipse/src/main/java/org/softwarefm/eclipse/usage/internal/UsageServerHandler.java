package org.softwarefm.eclipse.usage.internal;

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
import org.softwarefm.eclipse.usage.IUsageServer;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.strings.Strings;

public class UsageServerHandler implements HttpRequestHandler {
	
	private final IMultipleListenerList dummyListenerList = IMultipleListenerList.Utils.defaultList();
	private final IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
	private final ICallback<IUsage> callback;

	public UsageServerHandler( ICallback<IUsage> callback) {
		this.callback = callback;
	}

	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		RequestLine requestLine = request.getRequestLine();
		String method = requestLine.getMethod();
		if ("POST".equalsIgnoreCase(method) && request instanceof BasicHttpEntityEnclosingRequest) {
			HttpEntity httpEntity = ((BasicHttpEntityEnclosingRequest) request).getEntity();
			Usage usage = new Usage(dummyListenerList);
			byte[] byteArray = EntityUtils.toByteArray(httpEntity);
			String usageText = Strings.unzip(byteArray);
			persistance.populate(usage, usageText);
			ICallback.Utils.call(callback, usage);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		IUsageServer.Utils.usageServer(80, ICallback.Utils.<IUsage>sysoutCallback(), ICallback.Utils.sysErrCallback()).start();
		while (true)
			Thread.sleep(10000);
	}
}

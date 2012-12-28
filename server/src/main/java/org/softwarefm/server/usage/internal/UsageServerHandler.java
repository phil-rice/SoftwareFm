package org.softwarefm.server.usage.internal;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.RequestLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.softwarefm.server.AbstractHandler;
import org.softwarefm.server.usage.IUsageCallback;
import org.softwarefm.server.usage.IUsageCallbackAndGetter;
import org.softwarefm.server.usage.IUsageGetter;
import org.softwarefm.server.usage.IUsageServer;
import org.softwarefm.shared.usage.IUsage;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.exceptions.Exceptions;
import org.softwarefm.utilities.strings.Strings;

public class UsageServerHandler extends AbstractHandler {

	private final IMultipleListenerList dummyListenerList = IMultipleListenerList.Utils.defaultList();
	private final IUsagePersistance persistance;
	private final IUsageCallback callback;
	private final IUsageGetter usageGetter;

	public UsageServerHandler(IUsagePersistance persistance, IUsageCallbackAndGetter callbackAndGetter) {
		this(persistance, callbackAndGetter, callbackAndGetter);
	}

	public UsageServerHandler(IUsagePersistance persistance, IUsageCallback callback, IUsageGetter usageGetter) {
		this.persistance = persistance;
		this.callback = callback;
		this.usageGetter = usageGetter;
	}

	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		RequestLine requestLine = request.getRequestLine();
		String uri = requestLine.getUri();
		List<String> fragments = Strings.splitIgnoreBlanks(uri, "/");
		String method = requestLine.getMethod();
		int index = findMarker(method, fragments, "usage", 1);
		String user = fragments.get(index+1);
		if ("POST".equalsIgnoreCase(method) && request instanceof BasicHttpEntityEnclosingRequest) {
			try {
				HttpEntity httpEntity = ((BasicHttpEntityEnclosingRequest) request).getEntity();
				byte[] byteArray = EntityUtils.toByteArray(httpEntity);
				String text = Strings.unzip(byteArray);
				IUsageStats stats = persistance.parse(text);
				callback.process("", user, stats);
			} catch (Exception e) {
				response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
				response.setEntity(new StringEntity(Exceptions.serialize(e, "<br>\n"), "UTF-8"));
			}
		} else if ("GET".equalsIgnoreCase(method)) {
			IUsageStats stats = usageGetter.getStats(user);
			String result = persistance.save(stats);
			response.setEntity(new StringEntity(result));
		} else
			throw new HttpException("Illegal method " + request);
	}

	public static void main(String[] args) throws InterruptedException {
		IUsageServer.Utils.usageServer(80, ICallback.Utils.<IUsage> sysoutCallback(), ICallback.Utils.sysErrCallback()).start();
		while (true)
			Thread.sleep(10000);
	}
}

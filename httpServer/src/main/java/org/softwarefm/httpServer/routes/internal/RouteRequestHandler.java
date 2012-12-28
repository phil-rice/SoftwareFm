package org.softwarefm.httpServer.routes.internal;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.softwarefm.httpServer.IHttpRegistry;
import org.softwarefm.httpServer.StatusAndEntity;
import org.softwarefm.utilities.exceptions.Exceptions;
import org.softwarefm.utilities.http.HttpMethod;

public class RouteRequestHandler implements HttpRequestHandler {

	private final IHttpRegistry registry;

	public RouteRequestHandler(IHttpRegistry registry) {
		super();
		this.registry = registry;
	}

	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		try {
			String uri = request.getRequestLine().getUri();
			HttpMethod method = HttpMethod.valueOf(request.getRequestLine().getMethod());
			HttpEntity httpEntity = request instanceof BasicHttpEntityEnclosingRequest ? ((BasicHttpEntityEnclosingRequest) request).getEntity() : null;
			StatusAndEntity statusAndEntity = registry.process(method, uri, httpEntity);
			if (statusAndEntity != null) {
				response.setStatusCode(statusAndEntity.status);
				if (method != HttpMethod.HEAD)
					response.setEntity(statusAndEntity.entity);
				if (statusAndEntity.zipped)
					response.addHeader(new BasicHeader("Content-Encoding", "gzip"));
			}
		} catch (ThreadDeath e) {
			throw e;
		} catch (Throwable e) {
			response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			response.setEntity(new StringEntity(Exceptions.serialize(e, "<br>\n")));
		}
	}

}

package org.softwarefm.server;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpRequestHandler;

public abstract class AbstractHandler implements HttpRequestHandler{

	
	protected int findMarker(String method, List<String> fragments, String marker, int sizeAfterMarker) throws HttpException {
		int index = fragments.indexOf(marker);
		if (index == -1 || fragments.size() != index + sizeAfterMarker + 1)
			throw new HttpException("Malformed Url. Method " + method + " Url: " + fragments);
		return index;
	}

	protected void reply(HttpResponse response, String text) throws UnsupportedEncodingException {
		response.setEntity(new StringEntity(text));
	}

	
	
}

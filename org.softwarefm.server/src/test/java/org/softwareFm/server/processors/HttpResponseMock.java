package org.softwareFm.server.processors;

import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.params.HttpParams;

public class HttpResponseMock implements HttpResponse {
	@Override
	public void setEntity(HttpEntity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setParams(HttpParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHeaders(Header[] headers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHeader(String name, String value) {
	}

	@Override
	public void setHeader(Header header) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeHeaders(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeHeader(Header header) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HeaderIterator headerIterator(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HeaderIterator headerIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpParams getParams() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Header getLastHeader(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Header[] getHeaders(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Header getFirstHeader(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Header[] getAllHeaders() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsHeader(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addHeader(Header header) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStatusLine(ProtocolVersion ver, int code, String reason) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStatusLine(ProtocolVersion ver, int code) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStatusLine(StatusLine statusline) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStatusCode(int code) throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setReasonPhrase(String reason) throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLocale(Locale loc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StatusLine getStatusLine() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpEntity getEntity() {
		throw new UnsupportedOperationException();
	}
}
package org.softwareFm.server.processors.internal;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.entity.StringEntity;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.internal.GitTest;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

abstract public class AbstractProcessCallTest<T extends IProcessCall> extends GitTest {

	abstract protected T makeProcessor();

	protected T processor;
	protected IGitServer localGitServer;
	protected IGitServer remoteGitServer;

	protected void checkIgnores(String method) {
		String uri = "someUri";
		IProcessResult result = processor.process(makeRequestLine(method, uri), Maps.stringObjectMap("a", 1));
		assertNull(result);
	}
	protected void checkIgnores(String method, String uri) {
		IProcessResult result = processor.process(makeRequestLine(method, uri), Maps.stringObjectMap("a", 1));
		assertNull(result);
	}

	protected RequestLineMock makeRequestLine(String method, String uri) {
		return new RequestLineMock(method, uri);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		remoteGitServer = IGitServer.Utils.gitServer(remoteRoot, "not used");
		localGitServer = IGitServer.Utils.gitServer(localRoot, remoteGitServer.getRoot().getAbsolutePath());
		processor = makeProcessor();

	}

	protected Map<String, Object> makeDataMap(Map<String, Object> map) {
		return Maps.stringObjectMap(ServerConstants.dataParameterName, Json.toString(map));
	}

	protected void checkIgnoresNonePosts() {
		checkIgnores(ServerConstants.GET);
		checkIgnores(ServerConstants.DELETE);
	}

	protected void checkIgnoresNoneGet() {
		checkIgnores(ServerConstants.POST);
		checkIgnores(ServerConstants.DELETE);
	}

	protected void checkGetFromProcessor(String url, final Object... expected) {
		try {
			IProcessResult result = processor.process(makeRequestLine(ServerConstants.GET, url), emptyMap);
			checkStringResultWithMap(result, expected);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void checkErrorResult(IProcessResult result, final int expectedCode, final String expectedReason, final String expectedString) {
		final AtomicInteger count = new AtomicInteger();
		try {
			result.process(new HttpResponseMock() {
				@Override
				public void setEntity(HttpEntity entity) {
					try {
						count.incrementAndGet();
						assertTrue(entity instanceof StringEntity);
						StringEntity stringEntity = (StringEntity) entity;
						String string = Files.getText(stringEntity.getContent());
						assertEquals(expectedString, string);
					} catch (IOException e) {
						throw WrappedException.wrap(e);
					}
				}

				@Override
				public void setStatusCode(int code) throws IllegalStateException {
					assertEquals(expectedCode, code);
					count.incrementAndGet();
				}

				@Override
				public void setReasonPhrase(String reason) throws IllegalStateException {
					assertEquals(expectedReason, reason);
					count.incrementAndGet();
				}
			});
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
		assertEquals(3, count.get());// not perfect but easy
	}

	protected void checkStringResultWithMap(IProcessResult result, final Object... expected) {
		try {
			result.process(new HttpResponseMock() {
				@Override
				public void setEntity(HttpEntity entity) {
					try {
						assertTrue(entity instanceof StringEntity);
						StringEntity stringEntity = (StringEntity) entity;
						String string = Files.getText(stringEntity.getContent());
						Map<String, Object> actual = Json.mapFromString(string);
						assertEquals(Maps.stringObjectMap(expected), actual);
					} catch (IOException e) {
						throw WrappedException.wrap(e);
					}
				}
			});
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void checkStringResult(IProcessResult result, final String expected) {
		try {
			result.process(new HttpResponseMock() {
				@Override
				public void setEntity(HttpEntity entity) {
					try {
						assertTrue(entity instanceof StringEntity);
						StringEntity stringEntity = (StringEntity) entity;
						String string = Files.getText(stringEntity.getContent());
						assertEquals(expected, string);
					} catch (IOException e) {
						throw WrappedException.wrap(e);
					}
				}
			});
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void checkGetStringFromProcessor(String url, final String expected) {
		try {
			IProcessResult result = processor.process(makeRequestLine(ServerConstants.GET, url), emptyMap);
			checkStringResult(result, expected);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	static class RequestLineMock implements RequestLine {

		private final String method;
		private final String uri;

		public RequestLineMock(String method, String uri) {
			this.method = method;
			this.uri = uri;
		}

		@Override
		public String getMethod() {
			return method;
		}

		@Override
		public ProtocolVersion getProtocolVersion() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getUri() {
			return uri;
		}

		@Override
		public String toString() {
			return "RequestLineMock [method=" + method + ", uri=" + uri + "]";
		}

	}
}

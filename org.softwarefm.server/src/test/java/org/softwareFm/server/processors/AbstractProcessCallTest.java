package org.softwareFm.server.processors;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.entity.StringEntity;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.server.GitTest;

abstract public class AbstractProcessCallTest<T extends IProcessCall> extends GitTest {

	abstract protected T makeProcessor();

	protected T processor;

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
		processor = makeProcessor();

	}

	protected Map<String, Object> makeDataMap(Map<String, Object> map) {
		return Maps.stringObjectMap(CommonConstants.dataParameterName, Json.toString(map));
	}

	protected void checkIgnoresNonePosts() {
		checkIgnores(CommonConstants.GET);
		checkIgnores(CommonConstants.DELETE);
	}

	protected void checkIgnoresNoneGet() {
		checkIgnores(CommonConstants.POST);
		checkIgnores(CommonConstants.DELETE);
	}

	protected void checkGetFromProcessor(String url, final Object... expected) {
		try {
			IProcessResult result = processor.process(makeRequestLine(CommonConstants.GET, url), emptyMap);
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
			IProcessResult result = processor.process(makeRequestLine(CommonConstants.GET, url), emptyMap);
			checkStringResult(result, expected);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static class RequestLineMock implements RequestLine {

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

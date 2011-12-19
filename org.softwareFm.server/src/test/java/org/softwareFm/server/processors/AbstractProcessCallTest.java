package org.softwareFm.server.processors;

import java.util.Map;

import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.internal.GitTest;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

abstract public class AbstractProcessCallTest<T extends IProcessCall> extends GitTest {

	abstract protected T makeProcessor();

	protected T processor;
	protected IGitServer localGitServer;
	protected IGitServer remoteGitServer;

	protected void checkIgnores(String method) {
		String uri = "someUri";
		String result = processor.process(makeRequestLine(method, uri), Maps.stringObjectMap("a", 1));
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

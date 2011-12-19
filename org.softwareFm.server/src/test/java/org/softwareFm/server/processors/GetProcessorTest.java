package org.softwareFm.server.processors;

import java.util.Map;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

public class GetProcessorTest extends AbstractProcessCallTest<GetProcessor> {

	public void testIgnoresNoneGet() {
		checkIgnoresNoneGet();
	}

	public void testGetReturnsRepoAddressIfBelowOrAtRepository() {
		checkCreateRepository(remoteGitServer, "a/b");
		checkGet("a/b/c", ServerConstants.repoUrlKey, "a/b");
		checkGet("a/b", ServerConstants.repoUrlKey, "a/b");
	}

	public void testGetReturnsDataIfAboveRespository() {
		checkCreateRepository(remoteGitServer, "a/b/c");
		checkGet("a", ServerConstants.dataKey, Maps.stringObjectLinkedMap("b", emptyMap));
		checkGet("a/d", ServerConstants.dataKey, emptyMap);
	}

	private void checkGet(String url, Object... expected) {
		String string = processor.process(makeRequestLine(ServerConstants.GET, url), emptyMap);
		Map<String, Object> actual = Json.mapFromString(string);
		assertEquals(Maps.stringObjectMap(expected), actual);
	}

	@Override
	protected GetProcessor makeProcessor() {
		return new GetProcessor(remoteGitServer);
	}

}

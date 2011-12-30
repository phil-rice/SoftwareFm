package org.softwareFm.server.processors.internal;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.maps.UrlCache;

public class GitGetProcessorTest extends AbstractProcessCallTest<GitGetProcessor> {

	public void testIgnoresNoneGet() {
		checkIgnoresNoneGet();
	}

	public void testGetReturnsRepoAddressIfBelowOrAtRepository() {
		checkCreateRepository(remoteGitServer, "a/b");
		checkGetFromProcessor("a/b/c", ServerConstants.repoUrlKey, "a/b");
		checkGetFromProcessor("a/b", ServerConstants.repoUrlKey, "a/b");
	}

	public void testGetReturnsDataIfAboveRespository() {
		checkCreateRepository(remoteGitServer, "a/b/c");
		checkGetFromProcessor("a", ServerConstants.dataKey, Maps.stringObjectLinkedMap("b", emptyMap));
		checkGetFromProcessor("a/d", ServerConstants.dataKey, emptyMap);
	}

	@Override
	protected GitGetProcessor makeProcessor() {
		UrlCache<String> cache = new UrlCache<String>();
		return new GitGetProcessor(remoteGitServer, cache);
	}

}

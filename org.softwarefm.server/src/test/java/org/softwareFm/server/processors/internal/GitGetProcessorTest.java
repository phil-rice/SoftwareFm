package org.softwareFm.server.processors.internal;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.maps.UrlCache;

public class GitGetProcessorTest extends AbstractProcessCallTest<GitGetProcessor> {

	public void testIgnoresNoneGet() {
		checkIgnoresNoneGet();
	}

	public void testGetReturnsRepoAddressIfBelowOrAtRepository() {
		checkCreateRepository(remoteOperations, "a/b");
		checkGetFromProcessor("a/b/c", CommonConstants.repoUrlKey, "a/b");
		checkGetFromProcessor("a/b", CommonConstants.repoUrlKey, "a/b");
	}

	public void testGetReturnsDataIfAboveRespository() {
		checkCreateRepository(remoteOperations, "a/b/c");
		checkGetFromProcessor("a", CommonConstants.dataKey, Maps.stringObjectLinkedMap("b", emptyMap));
		checkGetFromProcessor("a/d", CommonConstants.dataKey, emptyMap);
	}

	@Override
	protected GitGetProcessor makeProcessor() {
		UrlCache<String> cache = new UrlCache<String>();
		return new GitGetProcessor(remoteOperations, cache);
	}

}

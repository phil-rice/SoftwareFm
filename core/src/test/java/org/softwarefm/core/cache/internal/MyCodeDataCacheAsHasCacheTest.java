package org.softwarefm.core.cache.internal;

import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.utilities.cache.AbstractHasCacheTest;

public class MyCodeDataCacheAsHasCacheTest extends AbstractHasCacheTest<String, String> {
	private ArtifactDataCache cache;

	@Override
	public void clearCaches() {
		cache.clearCaches();
	}

	@Override
	protected String key1() {
		return "name1";
	}

	@Override
	protected String key2() {
		return "name2";
	}

	@Override
	protected String value1() {
		return "value1";
	}

	@Override
	protected String value2() {
		return "value2";
	}

	@Override
	protected void putData(String key, String value) {
		cache.putMyCodeHtml(key, "sfmId", value);
	}

	@Override
	protected String getDataFor(String key) {
		return cache.myCodeHtml(key, "sfmId");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cache = (ArtifactDataCache) IArtifactDataCache.Utils.artifactDataCache();
	}

}

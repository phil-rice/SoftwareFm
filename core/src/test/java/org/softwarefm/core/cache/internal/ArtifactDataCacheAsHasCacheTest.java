package org.softwarefm.core.cache.internal;

import java.io.File;

import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.utilities.cache.AbstractHasCacheTest;

public class ArtifactDataCacheAsHasCacheTest extends AbstractHasCacheTest<File, CachedArtifactData> {

	private IArtifactDataCache cache;

	@Override
	public void clearCaches() {
		cache.clearCaches();
	}

	@Override
	protected File key1() {
		return new File("key1");
	}

	@Override
	protected File key2() {
		return new File("key2");
	}

	@Override
	protected CachedArtifactData value1() {
		return data(key1(), "value", 1);
	}

	@Override
	protected CachedArtifactData value2() {
		return data(key2(), "value", 2);
	}

	@Override
	protected void putData(File file, CachedArtifactData value) {
		assertEquals(file, value.artifactData.fileAndDigest.file);
		cache.addProjectData(value.artifactData);
	}

	@Override
	protected CachedArtifactData getDataFor(File key) {
		return cache.projectData(key);
	}

	private CachedArtifactData data(File file, String string, int i) {
		return CachedArtifactData.found(artifactData(file, string, i));
	}

	private ArtifactData artifactData(File file, String string, int i) {
		return new ArtifactData(new FileAndDigest(file, "some digest" + i), "g" + i, "a" + i, string + i);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cache = IArtifactDataCache.Utils.artifactDataCache();
	}

}

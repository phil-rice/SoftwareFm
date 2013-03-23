package org.softwarefm.core.cache.internal;

import java.io.File;

import junit.framework.TestCase;

import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.FileAndDigest;

public class ArtifactDataCacheNotFoundTest extends TestCase {

	private IArtifactDataCache cache;
	private final File file1 = new File("1");
	private final File file2 = new File("2");
	private final FileAndDigest fileAndDigest1 = new FileAndDigest(file1, "d1");
	private final FileAndDigest fileAndDigest2 = new FileAndDigest(file2, "d2");
	private final ArtifactData artifactData1 = new ArtifactData(fileAndDigest1, "g1", "a1", "v1");

	public void testNotFoundIsReturned() {
		cache.addNotFound(fileAndDigest1);
		cache.addNotFound(fileAndDigest2);
		assertEquals(CachedArtifactData.notFound(fileAndDigest1), cache.projectData(file1));
		assertEquals(CachedArtifactData.notFound(fileAndDigest2), cache.projectData(file2));
	}

	public void testLatestOfAddProjectDataOrNotFoundIsReturned() {
		for (int i = 0; i < 3; i++) {
			cache.addProjectData(artifactData1);
			assertEquals(CachedArtifactData.found(artifactData1), cache.projectData(file1));
			cache.clearCaches();

			cache.addNotFound(fileAndDigest1);
			assertEquals(CachedArtifactData.notFound(fileAndDigest1), cache.projectData(file1));
			cache.clearCaches();
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cache = IArtifactDataCache.Utils.artifactDataCache();
	}

}

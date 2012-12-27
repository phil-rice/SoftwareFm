package org.softwarefm.core.cache;

import java.io.File;

import junit.framework.TestCase;

import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.utilities.exceptions.CannotAddDuplicateKeyException;
import org.softwarefm.utilities.tests.Tests;

public class IDigestCacheTest extends TestCase {

	private final static FileAndDigest fdOne = new FileAndDigest(new File("one"), "01234");
	private final static FileAndDigest fdTwo = new FileAndDigest(new File("two"), "01234");
	private final static FileAndDigest fdThree = new FileAndDigest(new File("three"), "01234");
	private final static FileAndDigest fdFour = new FileAndDigest(new File("four"), "01234");

	private final static ArtifactData one = new ArtifactData(fdOne, "g", "a", "v");
	private final static ArtifactData two = new ArtifactData(fdTwo, "g", "a", "v");

	public void testGetUniqueCacheEachTime() {
		IArtifactDataCache cache1 = IArtifactDataCache.Utils.artifactDataCache();
		IArtifactDataCache cache2 = IArtifactDataCache.Utils.artifactDataCache();
		assertNotSame(cache1, cache2);
	}

	public void testWhenAddedUsesFileAsIndex() {
		IArtifactDataCache cache = IArtifactDataCache.Utils.artifactDataCache();
		assertNull(cache.projectData(new File("one")));
		cache.addProjectData(one);
		cache.addProjectData(two);
		cache.addNotFound(fdThree);
		cache.addNotFound(fdFour);
		assertEquals(CachedArtifactData.found(one), cache.projectData(new File("one")));
		assertEquals(CachedArtifactData.found(two), cache.projectData(new File("two")));
		assertEquals(CachedArtifactData.notFound(fdThree), cache.projectData(new File("three")));
		assertEquals(CachedArtifactData.notFound(fdFour), cache.projectData(new File("four")));
	}

	public void testCannotAddTwice() {
		final IArtifactDataCache cache = IArtifactDataCache.Utils.artifactDataCache();
		cache.addProjectData(one);
		cache.addNotFound(fdTwo);
		Tests.assertThrowsWithMessage("one", CannotAddDuplicateKeyException.class, new Runnable() {
			public void run() {
				cache.addProjectData(one);
			}
		});
		Tests.assertThrowsWithMessage("one", CannotAddDuplicateKeyException.class, new Runnable() {
			public void run() {
				cache.addNotFound(fdOne);
			}
		});
		Tests.assertThrowsWithMessage("two", CannotAddDuplicateKeyException.class, new Runnable() {
			public void run() {
				cache.addProjectData(two);
			}
		});
		Tests.assertThrowsWithMessage("two", CannotAddDuplicateKeyException.class, new Runnable() {
			public void run() {
				cache.addNotFound(fdTwo);
			}
		});
		assertEquals(CachedArtifactData.found(one), cache.projectData(new File("one")));
		assertEquals(CachedArtifactData.notFound(fdTwo), cache.projectData(new File("two")));
	}

	public void testClearEmptiesCacheButAllowsItToBeStillUsed() {
		IArtifactDataCache cache = IArtifactDataCache.Utils.artifactDataCache();
		assertNull(cache.projectData(new File("one")));
		cache.addProjectData(one);
		cache.addProjectData(two);
		cache.clearCaches();
		assertNull(cache.projectData(new File("one")));
		assertNull(cache.projectData(new File("two")));
		cache.addProjectData(one);
		cache.addProjectData(two);
		assertEquals(CachedArtifactData.found(one), cache.projectData(new File("one")));
		assertEquals(CachedArtifactData.found(two), cache.projectData(new File("two")));
	}

}

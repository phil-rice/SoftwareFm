package org.softwarefm.eclipse.cache;

import java.io.File;

import junit.framework.TestCase;

import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.utilities.exceptions.CannotAddDuplicateKeyException;
import org.softwarefm.utilities.tests.Tests;

public class IDigestCacheTest extends TestCase {

	private final static FileAndDigest fdOne = new FileAndDigest(new File("one"), "01234");
	private final static FileAndDigest fdTwo = new FileAndDigest(new File("two"), "01234");
	private final static FileAndDigest fdThree = new FileAndDigest(new File("three"), "01234");
	private final static FileAndDigest fdFour = new FileAndDigest(new File("four"), "01234");

	private final static ProjectData one = new ProjectData(fdOne, "g", "a", "v");
	private final static ProjectData two = new ProjectData(fdTwo, "g", "a", "v");

	public void testGetUniqueCacheEachTime() {
		IProjectDataCache cache1 = IProjectDataCache.Utils.projectDataCache();
		IProjectDataCache cache2 = IProjectDataCache.Utils.projectDataCache();
		assertNotSame(cache1, cache2);
	}

	public void testWhenAddedUsesFileAsIndex() {
		IProjectDataCache cache = IProjectDataCache.Utils.projectDataCache();
		assertNull(cache.projectData(new File("one")));
		cache.addProjectData(one);
		cache.addProjectData(two);
		cache.addNotFound(fdThree);
		cache.addNotFound(fdFour);
		assertEquals(CachedProjectData.found(one), cache.projectData(new File("one")));
		assertEquals(CachedProjectData.found(two), cache.projectData(new File("two")));
		assertEquals(CachedProjectData.notFound(fdThree), cache.projectData(new File("three")));
		assertEquals(CachedProjectData.notFound(fdFour), cache.projectData(new File("four")));
	}

	public void testCannotAddTwice() {
		final IProjectDataCache cache = IProjectDataCache.Utils.projectDataCache();
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
		assertEquals(CachedProjectData.found(one), cache.projectData(new File("one")));
		assertEquals(CachedProjectData.notFound(fdTwo), cache.projectData(new File("two")));
	}

	public void testClearEmptiesCacheButAllowsItToBeStillUsed() {
		IProjectDataCache cache = IProjectDataCache.Utils.projectDataCache();
		assertNull(cache.projectData(new File("one")));
		cache.addProjectData(one);
		cache.addProjectData(two);
		cache.clearCaches();
		assertNull(cache.projectData(new File("one")));
		assertNull(cache.projectData(new File("two")));
		cache.addProjectData(one);
		cache.addProjectData(two);
		assertEquals(CachedProjectData.found(one), cache.projectData(new File("one")));
		assertEquals(CachedProjectData.found(two), cache.projectData(new File("two")));
	}

}

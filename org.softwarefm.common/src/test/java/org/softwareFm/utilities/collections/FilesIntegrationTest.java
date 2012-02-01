package org.softwareFm.utilities.collections;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwareFm.utilities.tests.IIntegrationTest;

public class FilesIntegrationTest extends TestCase implements IIntegrationTest {

	private int count = 0;

	public void testDoOperationInLock() throws Exception {
		IServiceExecutor executor = IServiceExecutor.Utils.defaultExecutor();
		final int size = 100;// note this is a "square test...this many tries...and each try does it this many times
		final CountDownLatch latch = new CountDownLatch(size);
		String tempDir = System.getProperty("java.io.tmpdir");
		assertTrue(!tempDir.equals(""));
		File tests = new File(tempDir, "softwareFmTests");
		tests.mkdirs();
		final File dir = new File(tests, getClass().getSimpleName());
		dir.mkdirs();
		Files.deleteDirectory(dir);
		final String lockFileName = "lock";

		for (int i = 0; i < size; i++) {
			executor.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					for (int i = 0; i < size; i++)
						Files.doOperationInLock(dir, lockFileName, new IFunction1<File, Void>() {
							@Override
							public Void apply(File from) throws Exception {
								int thisValue = count;
								Thread.sleep(0);
								count = thisValue + 1;
								return null;
							}
						});
					latch.countDown();
					return null;
				}
			});
		}
		assertTrue(latch.await(50000, TimeUnit.MILLISECONDS));
		assertEquals(size * size, count);
	}

	public void testGetWhenDataDoesntExist() throws Exception {
	}

}

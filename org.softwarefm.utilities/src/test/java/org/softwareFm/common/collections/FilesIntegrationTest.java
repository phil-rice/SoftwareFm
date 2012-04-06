/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.collections;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.tests.IIntegrationTest;

public class FilesIntegrationTest extends TestCase implements IIntegrationTest {

	private int count = 0;

	public void testDoOperationInLock() throws Exception {
		IServiceExecutor executor = IServiceExecutor.Utils.defaultExecutor(getClass().getSimpleName()+"-{0}", 2);
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
			executor.submit(new IFunction1<IMonitor, Void>() {
				@Override
				public Void apply(IMonitor from) throws Exception {
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
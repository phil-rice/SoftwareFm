package org.arc4eclipse.utilities.monitor;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.monitor.SysoutMonitor;

public class SysoutMonitorTests extends TestCase {

	public void testCancelBehaviour() {
		SysoutMonitor monitor = new SysoutMonitor("", 1);
		assertFalse(monitor.cancelled());
		monitor.processed("", 100, 1); // i.e. 100 out of 1, so more than
		assertFalse(monitor.cancelled());
		monitor.processed("", 9, 10); // i.e. 9 out of 10 <---------- normal last value
		assertFalse(monitor.cancelled());
		monitor.processed("", 10, 10); // i.e. 10 out of 10
		assertFalse(monitor.cancelled());
		monitor.cancel();
		assertTrue(monitor.cancelled());
		assertTrue(monitor.cancelled());
		monitor.processed("", 5, 10); // i.e. we can still process even after cancel...cancel is just a request
		assertTrue(monitor.cancelled());
	}

	public void testDoneBehaviour() {
		SysoutMonitor monitor = new SysoutMonitor("", 1);
		assertFalse(monitor.done());
		monitor.processed("", 100, 1); // i.e. 100 out of 1, so more than
		assertFalse(monitor.done());
		monitor.processed("", 9, 10); // i.e. 9 out of 10 <---------- normal last value
		assertFalse(monitor.done());
		monitor.processed("", 10, 10); // i.e. 10 out of 10
		assertFalse(monitor.done());
		monitor.finish();
		assertTrue(monitor.done());
	}

	public void testSysoutWithQuanta() {
		checkQuanta(2, 1, 3, 5, 7, 9);
		checkQuanta(3, 2, 5, 8);
		checkQuanta(5, 4, 9);
		checkQuanta(-10, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		checkQuanta(-0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	private void checkQuanta(int quanta, Integer... expected) {
		final Collection<Integer> actual = Lists.newList();
		SysoutMonitor monitor = new SysoutMonitor("", quanta) {
			
			protected void display(String pattern, int done, int max) {
				actual.add(done);
			}
		};
		for (int i = 0; i < 10; i++)
			monitor.processed("", i, 10);
		assertEquals(Arrays.asList(expected), actual);
	}

}

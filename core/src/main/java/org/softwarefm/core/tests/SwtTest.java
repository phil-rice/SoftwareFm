/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.core.tests;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.exceptions.WrappedException;

abstract public class SwtTest extends ExecutorTestCase {

	protected Shell shell;
	protected Display display;

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.shell = new Shell();
		this.display = shell.getDisplay();
	}

	
	@Override
	protected void tearDown() throws Exception {
		try {
			Swts.Dispatch.dispatchUntilQueueEmpty(display);
		} finally {
			shell.dispose();
			super.tearDown();
		}
	}

	protected void dispatchUntilJobsFinished() {
		Swts.Dispatch.dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return getExecutor().getActiveCount() == 0;
			}
		});
	}

	protected void dispatchUntilQueueEmpty() {
		Swts.Dispatch.dispatchUntilQueueEmpty(display);
	}

	protected void kickAndDispatch(Future<?> future) {
		Swts.Dispatch.kickAndDispatch(display, future);
	}

	protected void execute(Runnable job) {
		final long initial = getExecutor().getCompletedTaskCount();
		Future<?> future = getExecutor().submit(job);
		try {
			Swts.Dispatch.dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
				
				public Boolean call() throws Exception {
					long count = getExecutor().getCompletedTaskCount();
					return count > initial;
				}
			});
			assertTrue(future.isDone());
			future.get(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

}
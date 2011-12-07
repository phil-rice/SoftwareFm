/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.display.swt;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.utilities.future.GatedMockFuture;

abstract public class SwtTest extends TestCase {

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
		dispatchUntilQueueEmpty();
		shell.dispose();
		super.tearDown();
	}

	protected void dispatchUntilTimeoutOrLatch(final CountDownLatch latch, long delay) throws InterruptedException {
		long start = System.currentTimeMillis();
		dispatchUntilQueueEmpty();
		while (!latch.await(10, TimeUnit.MILLISECONDS)) {
			if (System.currentTimeMillis() > delay + start)
				fail();
			dispatchUntilQueueEmpty();
			Thread.sleep(10);
		}
	}

	protected void select(Table table, int colIndex, String value) {
		for (int i = 0; i < table.getItemCount(); i++) {
			String text = table.getItem(i).getText(colIndex);
			if (text.equals(value)){
				table.select(i);
				return;
			}
		}
		throw new IllegalArgumentException(colIndex + ", " + value);
	}

	protected void kickAndDispatch(Future<?> future) {
		GatedMockFuture<?, ?> gatedMockFuture = (GatedMockFuture<?, ?>) future;
		gatedMockFuture.kick();
		dispatchUntilQueueEmpty();
	}

	protected void dispatchUntilQueueEmpty() {
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());
	}
}
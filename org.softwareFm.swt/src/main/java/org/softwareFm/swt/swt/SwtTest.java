/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.swt;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.future.GatedMockFuture;

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
		try {
			dispatchUntilQueueEmpty();
		} finally {
			shell.dispose();
			super.tearDown();
		}
	}

	protected void checkTextMatches(Composite values, String... expected) {
		Control[] children = values.getChildren();
		for (int i = 0; i < children.length; i++) {
			Control control = children[i];
			if (control instanceof Text)
				assertEquals(expected[i], ((Text) control).getText());
			else if (control instanceof StyledText)
				assertEquals(expected[i], ((StyledText) control).getText());
			else
				throw new IllegalArgumentException(control.getClass().getName());
		}
	}

	protected void checkLabelsMatch(Composite labels, String... expected) {
		Control[] children = labels.getChildren();
		assertEquals(expected.length, children.length);
		for (int i = 0; i < children.length; i++) {
			Label label = (Label) children[i];
			assertEquals(expected[i], label.getText());
		}
	}

	protected void dispatchUntilTimeoutOrLatch(final CountDownLatch latch, long delay) {
		try {
			long start = System.currentTimeMillis();
			dispatchUntilQueueEmpty();
			while (!latch.await(10, TimeUnit.MILLISECONDS)) {
				if (System.currentTimeMillis() > delay + start)
					fail();
				dispatchUntilQueueEmpty();
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void select(Table table, int colIndex, String value) {
		for (int i = 0; i < table.getItemCount(); i++) {
			String text = table.getItem(i).getText(colIndex);
			if (text.equals(value)) {
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

	public <T> T callInDispatch(Display display, final Callable<T> callable) {
		final AtomicReference<T> result = new AtomicReference<T>();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					result.set(callable.call());
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});

		return result.get();
	}

	protected void dispatchUntil(Display display, long delay, Callable<Boolean> callable) {
		long startTime = System.currentTimeMillis();
		try {
			dispatchUntilQueueEmpty();
			while (!callable.call() && System.currentTimeMillis() < startTime + delay) {
				dispatchUntilQueueEmpty();
				Thread.sleep(2);
			}
			checkAtEnd(display, callable);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private void checkAtEnd(Display display, Callable<Boolean> callable) {
		if (!callInDispatch(display, callable))
			fail();
	}

}
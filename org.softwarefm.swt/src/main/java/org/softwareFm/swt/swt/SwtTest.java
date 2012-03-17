/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.swt;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.future.GatedMockFuture;
import org.softwareFm.swt.composites.IHasComposite;

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

	public static void checkTextMatches(IHasComposite values, String... expected) {
		checkTextMatches(values.getComposite(), expected);
	}
	public static void checkTextMatches(Composite values, String... expected) {
		Control[] children = values.getChildren();
		for (int i = 0; i < expected.length; i++) {
			Control control = children[i*2+1];
			if (control instanceof Text)
				assertEquals(expected[i], ((Text) control).getText());
			else if (control instanceof StyledText)
				assertEquals(expected[i], ((StyledText) control).getText());
			else
				throw new IllegalArgumentException(control.getClass().getName());
		}
	}

	public static void checkLabelsMatch(IHasComposite editor, String... expected) {
		checkLabelsMatch(editor.getComposite(), expected);
	}

	public static void checkLabelsMatch(Composite editor, String... expected) {
		Control[] children = editor.getChildren();
		assertEquals(expected.length*2, children.length);
		for (int i = 0; i < expected.length; i++) {
			Label label = (Label) children[i * 2];
			assertEquals(expected[i], label.getText());
		}
	}

	public void dispatchUntilTimeoutOrLatch(final CountDownLatch latch, long delay) {
		dispatchUntilTimeoutOrLatch(display, latch, delay);
	}

	public static void dispatchUntilTimeoutOrLatch(Display display, final CountDownLatch latch, long delay) {
		try {
			long start = System.currentTimeMillis();
			dispatchUntilQueueEmpty(display);
			while (!latch.await(10, TimeUnit.MILLISECONDS)) {
				if (System.currentTimeMillis() > delay + start)
					fail();
				dispatchUntilQueueEmpty(display);
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void select(Table table, int colIndex, String value) {
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
		dispatchUntilQueueEmpty(display);
	}

	public static void dispatchUntilQueueEmpty(Display display) {
		Swts.dispatchUntilQueueEmpty(display);
	}

	public static <T> T callInDispatch(Display display, final Callable<T> callable) {
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

	public static void dispatchUntil(Display display, long delay, Callable<Boolean> callable) {
		long startTime = System.currentTimeMillis();
		try {
			dispatchUntilQueueEmpty(display);
			while (!callable.call() && System.currentTimeMillis() < startTime + delay) {
				dispatchUntilQueueEmpty(display);
				Thread.sleep(2);
			}
			checkAtEnd(display, callable);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private static void checkAtEnd(Display display, Callable<Boolean> callable) {
		if (!callInDispatch(display, callable))
			fail();
	}

	protected void checkCombo(Composite composite, int index, List<String> items, String text) {
		Combo combo = (Combo) composite.getChildren()[index];
		List<String> actual = Arrays.asList(combo.getItems());
		assertEquals(items, actual);
		assertEquals(text, combo.getText());

	}

	protected void checkRadioButton(Composite composite, int index, String text, boolean selected) {
		Button button = (Button) composite.getChildren()[index];
		assertEquals(text, button.getText());
		assertEquals(selected, button.getSelection());
	}

	protected void checkTableColumns(Table table, String... strings) {
		assertTrue(table.getHeaderVisible());
		for (int i = 0; i < strings.length; i++)
			assertEquals(strings[i], table.getColumn(i).getText());
		assertEquals(strings.length, table.getColumnCount());

	}

	protected void checkTable(Table table, int index, Object key, String... strings) {
		TableItem item = table.getItem(index);
		TableColumn[] columns = table.getColumns();
		assertEquals(key, item.getData());
		for (int i = 0; i < strings.length; i++)
			assertEquals(strings[i], item.getText(i));
		assertEquals(columns.length, strings.length);

	}

}
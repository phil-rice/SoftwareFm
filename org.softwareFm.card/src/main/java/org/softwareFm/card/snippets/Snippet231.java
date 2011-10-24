package org.softwareFm.card.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Snippet231 {

	public static void main(String[] args) {
		final int COLUMN_COUNT = 4;
		final int ITEM_COUNT = 8;
		final int TEXT_MARGIN = 3;
		Display display = new Display();
		Shell shell = new Shell(display);
		final Table table = new Table(shell, SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		for (int i = 0; i < COLUMN_COUNT; i++) {
			new TableColumn(table, SWT.NONE);
		}
		for (int i = 0; i < ITEM_COUNT; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			for (int j = 0; j < COLUMN_COUNT; j++) {
				String string = "item " + i + " col " + j;
				if ((i + j) % 3 == 1) {
					string += "\nnew line1";
				}
				if ((i + j) % 3 == 2) {
					string += "\nnew line1\nnew line2";
				}
				item.setText(j, string);
			}
		}

		/*
		 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly. Therefore, it is critical for performance that these methods be as efficient as possible.
		 */
		table.addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				String text = item.getText(event.index);
				Point size = event.gc.textExtent(text);
				event.width = size.x + 2 * TEXT_MARGIN;
				event.height = Math.max(event.height, size.y + TEXT_MARGIN);
			}
		});
		table.addListener(SWT.EraseItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.detail &= ~SWT.FOREGROUND;
			}
		});
		table.addListener(SWT.PaintItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				String text = item.getText(event.index);
				/* center column 1 vertically */
				int yOffset = 0;
				if (event.index == 1) {
					Point size = event.gc.textExtent(text);
					yOffset = Math.max(0, (event.height - size.y) / 2);
				}
				event.gc.drawText(text, event.x + TEXT_MARGIN, event.y + yOffset, true);
			}
		});

		for (int i = 0; i < COLUMN_COUNT; i++) {
			table.getColumn(i).pack();
		}
		table.pack();
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
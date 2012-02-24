package org.softwareFm.swt.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.okCancel.IOkCancel;
import org.softwareFm.swt.okCancel.internal.OkCancel;

abstract public class DataWithOkCancelComposite<T extends Control> extends DataComposite<T> implements IDataCompositeWithOkCancel<T> {

	private final OkCancel okCancel;

	public DataWithOkCancelComposite(Composite parent, CardConfig cardConfig, String cardType, String title) {
		this(parent, cardConfig, cardType, title, false);
	}

	public DataWithOkCancelComposite(Composite parent, CardConfig cardConfig, String cardType, String title, boolean titleIsKey) {
		super(parent, cardConfig, cardType, title, titleIsKey);
		okCancel = new OkCancel(getInnerBody(), getResourceGetter(), cardConfig.imageFn, new Runnable() {
			@Override
			public void run() {
				ok();
			}
		}, new Runnable() {
			@Override
			public void run() {
				cancel();
			}
		});
		getInnerBody().addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				int width = getInnerBody().getSize().x;
				int y = okCancel.getControl().getLocation().y - 1;
				e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_GRAY));
				e.gc.drawLine(0, y, width, y);
			}
		});
	}

	protected void moveOkCancelButtonsToEnd() {
		final Composite buttonComposite = okCancel.getComposite();
		okCancel.okButton.moveBelow(buttonComposite.getChildren()[buttonComposite.getChildren().length - 1]);
		okCancel.cancelButton.moveAbove(okCancel.okButton);
	}

	@Override
	public IOkCancel getFooter() {
		return okCancel;
	}

	abstract protected void ok();

	abstract protected void cancel();

}

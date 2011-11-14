package org.softwareFm.card.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.api.CardConfig;

@SuppressWarnings("unchecked")
public class ValueEditorLayout extends Layout {

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		IValueComposite<Control> c = (IValueComposite<Control>) composite;
		Point editorSize = c.getEditor().computeSize(wHint, hHint);
		Point okCancelSize = c.getOkCancel().getControl().computeSize(wHint, hHint);
		int height = hHint == SWT.DEFAULT ? c.getCardConfig().titleHeight + editorSize.y + okCancelSize.y : hHint;
		int width = composite.getParent().getClientArea().width; // want full width if can have it
		return new Point(width, height);
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		IValueComposite<Control> c = (IValueComposite<Control>) composite;
		Rectangle cb = composite.getBounds();
		Rectangle ca = composite.getClientArea();
		CardConfig cc = c.getCardConfig();

		c.getBody().setBounds(ca.x, ca.y + cc.topMargin + cc.titleHeight, ca.width, ca.height - cc.topMargin - cc.titleHeight);

		c.getInnerBody().setBounds(c.getBody().getClientArea());
		Rectangle cb_ca = c.getInnerBody().getClientArea();

		c.getTitle().getControl().setBounds(cb.x, cb.y, cb.width, cc.titleHeight + c.getCardConfig().topMargin);

		Control okCancelControl = c.getOkCancel().getControl();
		okCancelControl.pack();
		int okCancelWidth = okCancelControl.getSize().x;
		int okCancelHeight = okCancelControl.getSize().y;
		okCancelControl.setBounds(//
				cb_ca.x + cb_ca.width - okCancelWidth - 2,//
				cb_ca.y + cb_ca.height - okCancelHeight - 2,//
				okCancelWidth, okCancelHeight);

		Control editor = c.getEditor();
		int editorHeight = c.useAllHeight() ? //
		cb_ca.height - 2*cc.titleIndentY - okCancelControl.getSize().y //
				: editor.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;

		editor.setBounds(cb_ca.x + 1 + cc.titleIndentX,//
				cb_ca.y + cc.titleIndentY, //
				cb_ca.width - 2*cc.titleIndentX, //
				editorHeight);
	}

}

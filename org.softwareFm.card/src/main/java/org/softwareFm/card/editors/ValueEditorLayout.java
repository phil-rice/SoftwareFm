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
		ValueEditorComposite<Control> c = (ValueEditorComposite<Control>) composite;
		Point textSize = c.editorControl.computeSize(wHint, hHint);
		Point okCancelSize = c.okCancel.getControl().computeSize(wHint, hHint);
		int height = hHint == SWT.DEFAULT ? c.cardConfig.titleHeight + textSize.y + okCancelSize.y : hHint;
		int width = c.getParent().getClientArea().width; // want full width if can have it
		return new Point(width, height);
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		ValueEditorComposite<Control> c = (ValueEditorComposite<Control>) composite;
		Rectangle cb = c.getBounds();
		Rectangle ca = c.getClientArea();
		CardConfig cc = c.cardConfig;

		c.body.setBounds(ca.x, ca.y + cc.topMargin + cc.titleHeight, ca.width, ca.height - cc.topMargin - cc.titleHeight);

		c.innerBody.setBounds(c.body.getClientArea());
		Rectangle cb_ca = c.innerBody.getClientArea();

		c.titleLabel.getControl().setBounds(cb.x, cb.y, cb.width, cc.titleHeight + c.cardConfig.topMargin);

		Control okCancelControl = c.okCancel.getControl();
		okCancelControl.pack();
		int okCancelWidth = okCancelControl.getSize().x;
		int okCancelHeight = okCancelControl.getSize().y;
		okCancelControl.setBounds(//
				cb_ca.x + cb_ca.width - okCancelWidth - 2,//
				cb_ca.y + cb_ca.height - okCancelHeight - 2,//
				okCancelWidth, okCancelHeight);

		int editorHeight = c.useAllHeight() ? //
		cb_ca.height - 2*cc.titleIndentY - okCancelControl.getSize().y //
				: c.editorControl.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;

		c.editorControl.setBounds(cb_ca.x + 1 + cc.titleIndentX,//
				cb_ca.y + cc.titleIndentY, //
				cb_ca.width - 2*cc.titleIndentX, //
				editorHeight);
	}

}

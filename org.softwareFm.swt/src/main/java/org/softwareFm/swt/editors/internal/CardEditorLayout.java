/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.IValueComposite;

@SuppressWarnings("unchecked")
public class CardEditorLayout extends Layout {

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
		IValueComposite<Table> c = (IValueComposite<Table>) composite;
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

		Table editor = c.getEditor();
		int editorHeight = c.useAllHeight() ? //
		cb_ca.height - okCancelControl.getSize().y - 4 //
				: editor.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;

		editor.setBounds(cb_ca.x, cb_ca.y, cb_ca.width, editorHeight);
		ICard.Utils.setCardTableColumnWidths(editor, cc);

	}

}
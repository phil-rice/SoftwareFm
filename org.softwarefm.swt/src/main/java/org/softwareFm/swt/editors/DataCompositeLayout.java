/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.swt.configuration.CardConfig;

@SuppressWarnings("unchecked")
public class DataCompositeLayout extends Layout {

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		IDataComposite<Control> c = (IDataComposite<Control>) composite;
		Point editorSize = c.getEditor().computeSize(wHint, hHint);
		int height = hHint == SWT.DEFAULT ? c.getCardConfig().titleHeight + editorSize.y : hHint;
		int width = composite.getParent().getClientArea().width; // want full width if can have it
		return new Point(width, height);
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		IDataComposite<Control> c = (IDataComposite<Control>) composite;
		Rectangle cb = composite.getBounds();
		Rectangle ca = composite.getClientArea();
		CardConfig cc = c.getCardConfig();
		Control header = c.getTitle().getControl();
		header.setBounds(ca.x, ca.y, ca.width, cc.titleHeight + cc.topMargin);
		
		c.getBody().setBounds(ca.x, ca.y + cc.topMargin + cc.titleHeight, ca.width, ca.height - cc.topMargin - cc.titleHeight);

		c.getInnerBody().setBounds(c.getBody().getClientArea());
		Rectangle cb_ca = c.getInnerBody().getClientArea();

		c.getTitle().getControl().setBounds(cb.x, cb.y, cb.width, cc.titleHeight + c.getCardConfig().topMargin);

		Control editor = c.getEditor();
		int editorHeight = c.useAllHeight() ? cb_ca.height - 2 * cc.editorIndentY : editor.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;

		editor.setBounds(cb_ca.x + 1 + cc.editorIndentX,//
				cb_ca.y + cc.editorIndentY, //
				cb_ca.width - 2 * cc.editorIndentX, //
				editorHeight);
	}

}
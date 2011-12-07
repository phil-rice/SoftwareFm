package org.softwareFm.collections.explorer.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.card.IHasCardConfig;
import org.softwareFm.card.configuration.CardConfig;

/** COmposite must implements IHasCardConfig, and have three children: Title, Card, OKCancel */
public class AddCardLayout extends Layout {

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		Control[] children = composite.getChildren();
		assert children.length == 3;
		CardConfig cardConfig = ((IHasCardConfig) composite).getCardConfig();
		Control title = children[0];
		Control card = children[1];
		Control okCancel = children[2];

		Point titleSize = title.computeSize(wHint, cardConfig.titleHeight);
		Point okCancelSize = okCancel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point cardSize = card.computeSize(wHint, hHint == SWT.DEFAULT ? SWT.DEFAULT : hHint - titleSize.y);
		return new Point(Math.max(titleSize.x, cardSize.x), titleSize.y + cardSize.y + okCancelSize.y);
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		Control[] children = composite.getChildren();
		assert children.length == 3;
		CardConfig cc = ((IHasCardConfig) composite).getCardConfig();
		Control title = children[0];
		Control card = children[1];
		Control okCancel = children[2];

		// System.out.println(Swts.boundsUpToShell(composite));
		Rectangle ca = composite.getClientArea();
		Point okCancelSize = okCancel.computeSize(ca.width, SWT.DEFAULT);

		title.setBounds(ca.x, ca.y, ca.width, cc.titleHeight + cc.topMargin);

		card.setBounds(ca.x, ca.y + cc.titleHeight + cc.topMargin, ca.width, ca.height - cc.titleHeight - okCancelSize.y - cc.topMargin);

		okCancel.setSize(ca.width, okCancelSize.y);
		okCancel.setLocation(ca.x, ca.height - okCancelSize.y);
//		composite.redraw();
	}

}
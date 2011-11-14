package org.softwareFm.card.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.api.CardConfig;

public class OKCancelWithBorder implements  IOkCancel {

	static public class OKCancelWithBorderLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			Rectangle b = composite.getBounds();
			Rectangle ca = composite.getClientArea();
			int x = 0;
			int y = 0;
			for (Control child : composite.getChildren()) {
				Point size = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				x = x + size.x;
				y = Math.max(y, size.y);
			}
			return new Point(x + b.width - ca.width, y + b.height - ca.height);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			int width = 0;
			for (Control child : composite.getChildren()) {
				child.pack();
				width+= child.getSize().x;
			}
			Rectangle ca = composite.getClientArea();
			int x = ca.x + ca.width -width;
			int y = ca.y;
			for (Control child : composite.getChildren()) {
				child.setLocation(x, y);
				x = x + child.getSize().x;
			}
		}
	}

	private Composite content;
	private OkCancel okCancel;

	public OKCancelWithBorder(Composite parent, final CardConfig cardConfig, final Runnable onAccept, final Runnable onCancel) {
		content = new Composite(parent, SWT.NULL) {
			@Override
			public Rectangle getClientArea() {
				Rectangle ca = super.getClientArea();
				return new Rectangle(ca.x + cardConfig.leftMargin, ca.y + cardConfig.topMargin, ca.width - cardConfig.leftMargin - cardConfig.rightMargin, ca.height - cardConfig.topMargin - cardConfig.bottomMargin);
			}
		};
		okCancel = new OkCancel(content, cardConfig, onAccept, onCancel);

		content.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				Rectangle ca = content.getClientArea();
				Rectangle r = new Rectangle(ca.x - cardConfig.cornerRadiusComp, ca.y - cardConfig.cornerRadiusComp, //
						ca.width + 2 * cardConfig.cornerRadiusComp, //
						ca.height + 2 * cardConfig.cornerRadiusComp);
				e.gc.drawRoundRectangle(r.x, r.y, r.width, r.height, cardConfig.cornerRadius, cardConfig.cornerRadius);
			}
		});
	}

	@Override
	public void setOkEnabled(boolean enabled) {
		okCancel.setOkEnabled(enabled);
	}

	@Override
	public boolean isOkEnabled() {
		return okCancel.isOkEnabled();
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public void ok() {
		okCancel.ok();
	}

	@Override
	public void cancel() {
		okCancel.cancel();
	}

	public void setLayout(Layout layout) {
		content.setLayout(layout);
	}

}

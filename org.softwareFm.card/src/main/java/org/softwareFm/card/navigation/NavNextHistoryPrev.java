package org.softwareFm.card.navigation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.internal.History;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.title.TitleAnchor;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;

public class NavNextHistoryPrev<T> implements IHasControl {

	private final NavNextHistoryPrevComposite<T> content;

	static public class NavNextHistoryPrevComposite<T> extends Composite {
		private final History<T> history;
		private final Label prevButton;
		private final NavHistoryCombo<T> navCombo;
		private final Label nextButton;
		private final NavNextHistoryPrevConfig<T> navNextHistoryPrevConfig;

		public NavNextHistoryPrevComposite(Composite parent, final NavNextHistoryPrevConfig<T> navNextHistoryPrevConfig) {
			super(parent, SWT.NULL);
			this.navNextHistoryPrevConfig = navNextHistoryPrevConfig;
			history = new History<T>();

			final Image prevImage = Functions.call(navNextHistoryPrevConfig.imageFn, TitleAnchor.previousKey);
			final Image historyImage = Functions.call(navNextHistoryPrevConfig.imageFn, TitleAnchor.historyKey);
			final Image nextImage = Functions.call(navNextHistoryPrevConfig.imageFn, TitleAnchor.nextKey);

			prevButton = Swts.makeImageButton(this, prevImage, new Runnable() {
				@Override
				public void run() {
					ICallback.Utils.call(navNextHistoryPrevConfig.gotoCallback, history.prev());
					updateNextPrevButtons();
				}
			});
			navCombo = new NavHistoryCombo<T>(this, history, navNextHistoryPrevConfig.gotoCallback, navNextHistoryPrevConfig.stringFn);
			navCombo.getControl().addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					Combo combo = navCombo.combo;
					Rectangle clientArea = combo.getClientArea();
					e.gc.setBackground(combo.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
					e.gc.fillRectangle(clientArea);
					e.gc.drawImage(historyImage, 0, +1);
				}
			});
			nextButton = Swts.makeImageButton(this, nextImage, new Runnable() {
				@Override
				public void run() {
					ICallback.Utils.call(navNextHistoryPrevConfig.gotoCallback, history.next());
					updateNextPrevButtons();
				}
			});
			nextButton.setImage(nextImage);
		}

		private void updateNextPrevButtons() {
			nextButton.setEnabled(history.hasNext());
			prevButton.setEnabled(history.hasPrev());
		}

		@Override
		public void setLayout(Layout layout) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Rectangle getClientArea() {
			Rectangle ca = super.getClientArea();
			return new Rectangle(ca.x + navNextHistoryPrevConfig.leftMargin, //
					ca.y + navNextHistoryPrevConfig.topMargin, //
					ca.width = navNextHistoryPrevConfig.leftMargin - navNextHistoryPrevConfig.rightMargin, //
					ca.height - navNextHistoryPrevConfig.topMargin - navNextHistoryPrevConfig.bottomMargin);
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			return new Point(navNextHistoryPrevConfig.leftMargin + 3 * navNextHistoryPrevConfig.navIconWidth + navNextHistoryPrevConfig.rightMargin, //
					navNextHistoryPrevConfig.topMargin + navNextHistoryPrevConfig.height + navNextHistoryPrevConfig.bottomMargin);
		}

		@Override
		public void layout() {
			Rectangle ca = getClientArea();
			int navIconWidth = navNextHistoryPrevConfig.navIconWidth;
			int height = navNextHistoryPrevConfig.height;
			int x = ca.x;
			int y = ca.y;
			for (Control control: getChildren()){
				control.setBounds(x, y, navIconWidth, height);
				x += navIconWidth;
			}
		}

	}

	public NavNextHistoryPrev(Composite parent, final NavNextHistoryPrevConfig<T> navNextHistoryPrevConfig) {
		content = new NavNextHistoryPrevComposite<T>(parent, navNextHistoryPrevConfig);
	}

	@Override
	public Control getControl() {
		return content;
	}

}

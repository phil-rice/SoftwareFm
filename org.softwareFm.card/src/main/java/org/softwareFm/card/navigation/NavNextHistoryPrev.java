package org.softwareFm.card.navigation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts.Button;
import org.softwareFm.softwareFmImages.title.TitleAnchor;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.history.History;
import org.softwareFm.utilities.history.IHistory;

public class NavNextHistoryPrev<T> implements IHasControl {

	private final NavNextHistoryPrevComposite<T> content;

	static public class NavNextHistoryPrevComposite<T> extends Composite {
		private final History<T> history;
		private final Label prevButton;
		private final NavHistoryCombo<T> navCombo;
		private final Label nextButton;
		private final NavNextHistoryPrevConfig<T> config;

		public NavNextHistoryPrevComposite(Composite parent, final NavNextHistoryPrevConfig<T> config) {
			super(parent, SWT.NULL);
			this.config = config;
			history = new History<T>();

			final Image prevImage = Functions.call(config.imageFn, TitleAnchor.previousKey);
			final Image historyImage = Functions.call(config.imageFn, TitleAnchor.historyKey);
			final Image nextImage = Functions.call(config.imageFn, TitleAnchor.nextKey);

			prevButton = Button.makeImageButton(this, prevImage, new Runnable() {
				@Override
				public void run() {
					ICallback.Utils.call(config.gotoCallback, history.prev());
					updateNextPrevButtons();
				}
			});
			navCombo = new NavHistoryCombo<T>(this, history, config.gotoCallback, config);
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
			nextButton = Button.makeImageButton(this, nextImage, new Runnable() {
				@Override
				public void run() {
					ICallback.Utils.call(config.gotoCallback, history.next());
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
			return new Rectangle(ca.x + config.leftMargin, //
					ca.y + config.topMargin, //
					ca.width - config.leftMargin - config.rightMargin, //
					ca.height - config.topMargin - config.bottomMargin);
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			return new Point(config.leftMargin + 3 * config.navIconWidth + config.rightMargin, //
					config.topMargin + config.height + config.bottomMargin);
		}

		@Override
		public void layout() {
			Rectangle ca = getClientArea();
			int navIconWidth = config.navIconWidth;
			int height = config.height;
			int x = ca.x;
			int y = ca.y;
			prevButton.setBounds(x, y, navIconWidth, height);
			x += navIconWidth;
			navCombo.getControl().setBounds(x, y, navIconWidth, height);
			x += navIconWidth;
			nextButton.setBounds(x, y, navIconWidth, height);
		}

	}

	public NavNextHistoryPrev(Composite parent, final NavNextHistoryPrevConfig<T> navNextHistoryPrevConfig) {
		content = new NavNextHistoryPrevComposite<T>(parent, navNextHistoryPrevConfig);
	}

	@Override
	public Control getControl() {
		return content;
	}

	public IHistory<T> getHistory() {
		return content.history;
	}

	public void visiting(T place) {
		content.history.push(place);
		content.navCombo.updateFromHistory();
		content.updateNextPrevButtons();

	}

	public void layout() {
		content.layout();
	}

	public void setBackground(Color background) {
		content.setBackground(background);
		content.nextButton.setBackground(background);
		content.navCombo.getControl().setBackground(background);
		content.prevButton.setBackground(background);

	}

}

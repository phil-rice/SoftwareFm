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
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Button;
import org.softwareFm.softwareFmImages.title.TitleAnchor;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.history.IHistory;
import org.softwareFm.utilities.history.IHistoryListener;

public class NavNextHistoryPrev<T> implements IHasControl {

	private final NavNextHistoryPrevComposite<T> content;
	private static int globalId = 0;
	
	public static class NavNextHistoryPrevLayout<T> extends Layout{

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			@SuppressWarnings("unchecked")
			NavNextHistoryPrevComposite<T> nav = (NavNextHistoryPrevComposite<T>) composite;
			NavNextHistoryPrevConfig<T> config = nav.config;
			return new Point(config.leftMargin + 3 * config.navIconWidth + config.rightMargin, //
					config.topMargin + config.height + config.bottomMargin);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			@SuppressWarnings("unchecked")
			NavNextHistoryPrevComposite<T> nav = (NavNextHistoryPrevComposite<T>) composite;
			Rectangle ca = nav.getClientArea();
			System.out.println("  NHP " + Swts.boundsUpToShell(composite)  + " clientAreas: " + Swts.clientAreasUpToShell(composite));
			int navIconWidth = nav.config.navIconWidth;
			int height = nav.config.height;
			int x = ca.x;
			int y = ca.y;
			nav.prevButton.setBounds(x, y, navIconWidth, height);
			x += navIconWidth;
			nav.	navCombo.getControl().setBounds(x, y, navIconWidth, height);
			x += navIconWidth;
			nav.	nextButton.setBounds(x, y, navIconWidth, height);
		}

	}

	static public class NavNextHistoryPrevComposite<T> extends Composite {
		private final Label prevButton;
		private final NavHistoryCombo<T> navCombo;
		private final Label nextButton;
		private final NavNextHistoryPrevConfig<T> config;
		private final IHistory<T> history;
		private final int id;

		public NavNextHistoryPrevComposite(Composite parent, final NavNextHistoryPrevConfig<T> config, final IHistory<T> history) {
			super(parent, SWT.NULL);
			id = globalId++;
			this.config = config;
			this.history = history;

			final Image prevImage = Functions.call(config.imageFn, TitleAnchor.previousKey);
			final Image historyImage = Functions.call(config.imageFn, TitleAnchor.historyKey);
			final Image nextImage = Functions.call(config.imageFn, TitleAnchor.nextKey);

			prevButton = Button.makeImageButton(this, prevImage, new Runnable() {
				@Override
				public void run() {
					ICallback.Utils.call(config.gotoCallback, history.previous());
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
			history.addHistoryListener(new IHistoryListener<T>() {
				@Override
				public void changingTo(T newValue) {
					updateNextPrevButtons();
				}
			});
		}

		private void updateNextPrevButtons() {
			boolean hasNext = history.hasNext();
			boolean hasPrevious = history.hasPrevious();
			nextButton.setEnabled(hasNext);
			prevButton.setEnabled(hasPrevious);
		}

		@Override
		public Rectangle getClientArea() {
			Rectangle ca = super.getClientArea();
			return new Rectangle(ca.x + config.leftMargin, //
					ca.y + config.topMargin, //
					ca.width - config.leftMargin - config.rightMargin, //
					ca.height - config.topMargin - config.bottomMargin);
		}

		
	}

	public NavNextHistoryPrev(Composite parent, final NavNextHistoryPrevConfig<T> navNextHistoryPrevConfig, IHistory<T> history) {
		content = new NavNextHistoryPrevComposite<T>(parent, navNextHistoryPrevConfig, history);
	}

	@Override
	public Control getControl() {
		return content;
	}

	public IHistory<T> getHistory() {
		return content.history;
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

	public void setLayout(Layout layout) {
		content.setLayout(layout);
		
	}

}

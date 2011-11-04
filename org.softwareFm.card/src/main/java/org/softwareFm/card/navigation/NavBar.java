package org.softwareFm.card.navigation;

import org.eclipse.jface.resource.ImageRegistry;
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
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.internal.History;
import org.softwareFm.card.internal.details.TitleSpec;
import org.softwareFm.card.internal.title.TitlePaintListener;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.title.TitleAnchor;
import org.softwareFm.utilities.callbacks.ICallback;

public class NavBar implements IHasComposite, ITitleBarForCard {
	static class NavBarComposite extends Composite {

		private final History<String> history;
		private final String rootUrl;
		private final ICallback<String> callbackToGotoUrl;
		private final Label prevButton;
		private final Label nextButton;
		private final int height;
		private final CardConfig cardConfig;
		private final NavHistoryCombo navCombo;
		private String url;

		public NavBarComposite(Composite parent, CardConfig cardConfig, String rootUrl, final ICallback<String> callbackToGotoUrl) {
			super(parent, SWT.NULL);
			final ImageRegistry imageRegistry = new ImageRegistry();
			new BasicImageRegisterConfigurator().registerWith(parent.getDisplay(), imageRegistry);
			this.height = cardConfig.titleHeight;
			this.cardConfig = cardConfig;
			this.rootUrl = rootUrl;
			this.callbackToGotoUrl = callbackToGotoUrl;
			history = new History<String>();

			prevButton = Swts.makeImageButton(this, imageRegistry.get(TitleAnchor.previousKey), new Runnable() {
				@Override
				public void run() {
					ICallback.Utils.call(callbackToGotoUrl, history.prev());
					updateNextPrevButtons();
				}
			});
			navCombo = new NavHistoryCombo(this, history, callbackToGotoUrl);
			navCombo.getControl().addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					Combo combo = navCombo.combo;
					Rectangle clientArea = combo.getClientArea();
					e.gc.setBackground(combo.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
					e.gc.fillRectangle(clientArea);
					Image image = imageRegistry.get(TitleAnchor.historyKey);
					e.gc.drawImage(image, 0, -1);
					// e.gc.drawRectangle(clientArea.x, clientArea.y, clientArea.width - 1, clientArea.height);
				}
			});
			nextButton = Swts.makeImageButton(this, imageRegistry.get(TitleAnchor.nextKey), new Runnable() {
				@Override
				public void run() {
					ICallback.Utils.call(callbackToGotoUrl, history.next());
					updateNextPrevButtons();
				}
			});
			nextButton.setImage(imageRegistry.get(TitleAnchor.nextKey));
			updateNextPrevButtons();
			addPaintListener(new TitlePaintListener(cardConfig, TitleSpec.noTitleSpec(getBackground()), ""));
		}

		public void setUrl(String url) {
			this.url = url;
			if (!url.startsWith(rootUrl))
				throw new IllegalArgumentException("rooturl: " + rootUrl + " url: " + url);
			history.push(url);
			navCombo.updateFromHistory();
			updateNextPrevButtons();
			String endOfUrl = url.substring(rootUrl.length());
			String[] fragments = endOfUrl.split("/");
			Swts.removeChildrenAfter(this, nextButton);
			String thisUrl = rootUrl;
			for (final String string : fragments)
				if (string.length() > 0) {
					String parentUrl = thisUrl;
					new NavCombo(this, cardConfig, parentUrl, string, callbackToGotoUrl);
					thisUrl += "/" + string;
					new NavButton(this, thisUrl, callbackToGotoUrl);

				}
			new NavCombo(this, cardConfig, url, "", callbackToGotoUrl);
			layout();
			getParent().layout();
			redraw();
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
		public Point computeSize(int wHint, int hHint) {
			Rectangle clientArea = getClientArea();
			int x = clientArea.x+cardConfig.leftMargin;
			for (Control control : getChildren())
				if (control instanceof Combo) {
					x += cardConfig.navIconWidth;
				} else
					x += control.computeSize(wHint, hHint).x;
			return new Point(x, height);
		}

		@Override
		public void layout() {
			Rectangle clientArea = getClientArea();
			Control[] children = getChildren();

			for (Control control : children) {
				if (control instanceof Combo) {
					control.setSize(cardConfig.navIconWidth, height);
				} else {
					int width = control.computeSize(SWT.DEFAULT, clientArea.height).x;
					control.setSize(width, clientArea.height);
				}
			}
			int i = 3;
			if (isTooBig(clientArea)) {
				while (isTooBig(clientArea) && i < children.length) {
					Control child = children[i];
					if (child instanceof Label) {
						child.setSize(cardConfig.compressedNavTitleWidth, height);
						child.setToolTipText(((Label) child).getText());
					}
					i++;
				}
			}
			int x = clientArea.x+cardConfig.leftMargin;
			int y = clientArea.y+2;
			for (Control control : getChildren())
				if (control instanceof Combo) {
					control.setLocation(x, y);
					x += cardConfig.navIconWidth;
				} else {
					control.setLocation(x, y );
					int width = control.getSize().x;
					x += width;
				}
		}

		private boolean isTooBig(Rectangle clientArea) {
			int width = 0;
			for (Control child : getChildren())
				width += child.getSize().x;
			return width > clientArea.x + clientArea.width;
		}

	}

	private final NavBarComposite content;

	public NavBar(Composite parent, CardConfig cardConfig, String rootUrl, ICallback<String> callbackToGotoUrl) {
		content = new NavBarComposite(parent, cardConfig, rootUrl, callbackToGotoUrl);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public String getRootUrl() {
		return content.rootUrl;
	}

	public String getCurrentUrl() {
		return content.url;
	}

	public History<String> getHistory() {
		return content.history;
	}

	@Override
	public void setUrl(ICard card) {
		content.setUrl(card.url());
	}

	public void addCardSelectedListener(ICardSelectedListener listener) {
	}

}

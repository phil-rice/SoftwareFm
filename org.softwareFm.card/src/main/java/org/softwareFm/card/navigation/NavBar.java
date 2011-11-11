package org.softwareFm.card.navigation;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
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
import org.softwareFm.card.title.TitlePaintListener;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.history.History;
import org.softwareFm.utilities.history.IHistory;

public class NavBar implements IHasComposite, ITitleBarForCard {
	static class NavBarComposite extends Composite {

		private final String rootUrl;
		private final ICallback<String> callbackToGotoUrl;
		private final int height;
		private final CardConfig cardConfig;
		private String url;
		private final TitlePaintListener listener;
		private NavNextHistoryPrev<String> navNextHistoryPrev;

		public NavBarComposite(Composite parent, CardConfig cardConfig, String rootUrl, final ICallback<String> callbackToGotoUrl) {
			super(parent, SWT.NULL);
			final ImageRegistry imageRegistry = new ImageRegistry();
			new BasicImageRegisterConfigurator().registerWith(parent.getDisplay(), imageRegistry);
			this.height = cardConfig.titleHeight;
			this.cardConfig = cardConfig;
			this.rootUrl = rootUrl;
			this.callbackToGotoUrl = callbackToGotoUrl;
			IFunction1<String, Image> imageFn = new IFunction1<String, Image>() {
				@Override
				public Image apply(String from) throws Exception {
					return imageRegistry.get(from);
				}
			};
			navNextHistoryPrev = new NavNextHistoryPrev<String>(this, new NavNextHistoryPrevConfig<String>(height, imageFn, Functions.<String> toStringFn(), callbackToGotoUrl), new History<String>());
			listener = new TitlePaintListener(cardConfig, TitleSpec.noTitleSpec(getBackground()), "");
			addPaintListener(listener);
		}

		public void setUrl(String url, TitleSpec titleSpec) {
			this.url = url;
			listener.setTitleAndTitleSpec("", titleSpec);
			// setBackground(titleSpec.background);
			if (!url.startsWith(rootUrl))
				throw new IllegalArgumentException("rooturl: " + rootUrl + " url: " + url);
			navNextHistoryPrev.visiting(url);
			navNextHistoryPrev.setBackground(titleSpec.background);
			String endOfUrl = url.substring(rootUrl.length());
			String[] fragments = endOfUrl.split("/");
			Swts.removeChildrenAfter(this, navNextHistoryPrev.getControl());
			String thisUrl = rootUrl;
			for (final String string : fragments)
				if (string.length() > 0) {
					String parentUrl = thisUrl;
					new NavCombo(this, cardConfig, parentUrl, string, callbackToGotoUrl);
					thisUrl += "/" + string;
					new NavButton(this, thisUrl, callbackToGotoUrl);
				}
			new NavCombo(this, cardConfig, url, "", callbackToGotoUrl);
			for (Control control : getChildren())
				control.setBackground(titleSpec.background);
			layout();
			getParent().layout();
			redraw();
		}

		@Override
		public void setLayout(Layout layout) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			Rectangle clientArea = getClientArea();
			int x = clientArea.x + cardConfig.leftMargin;
			for (Control control : getChildren())
				if (control instanceof Combo) {
					x += cardConfig.navIconWidth;
				} else
					x += control.computeSize(wHint, hHint).x;
			return new Point(x, height);
		}

		@Override
		public void layout() {
			Rectangle ca = getClientArea();
			Control[] children = getChildren();
			navNextHistoryPrev.layout();
			navNextHistoryPrev.getControl().setLocation(ca.x, ca.y);
			for (Control control : children) {
				if (control instanceof Combo) {
					control.setSize(cardConfig.navIconWidth, height);
				} else {
					int width = control.computeSize(SWT.DEFAULT, ca.height).x;
					control.setSize(width, ca.height);
				}
			}
			int i = 1;
			if (isTooBig(ca)) {
				while (isTooBig(ca) && i < children.length) {
					Control child = children[i];
					if (child instanceof Label) {
						child.setSize(cardConfig.compressedNavTitleWidth, height);
						child.setToolTipText(((Label) child).getText());
					}
					i++;
				}
			}
			int x = ca.x + cardConfig.leftMargin;
			int y = ca.y + 2;
			for (Control control : getChildren())
				if (control instanceof Combo) {
					control.setLocation(x, y);
					x += cardConfig.navIconWidth;
				} else {
					control.setLocation(x, y);
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

		@Override
		public Rectangle getClientArea() {
			Rectangle clientArea = super.getClientArea();
			return new Rectangle(clientArea.x, clientArea.y, clientArea.width - listener.getTitleSpec().rightIndent, clientArea.height);
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

	public IHistory<String> getHistory() {
		return content.navNextHistoryPrev.getHistory();
	}

	@Override
	public void setUrl(ICard card) {
		TitleSpec titleSpec = Functions.call(card.cardConfig().titleSpecFn, card);
		content.setUrl(card.url(), titleSpec);
	}

	public void addCardSelectedListener(ICardSelectedListener listener) {
	}

}

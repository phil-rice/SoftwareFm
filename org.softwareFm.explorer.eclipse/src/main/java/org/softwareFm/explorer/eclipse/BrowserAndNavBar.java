package org.softwareFm.explorer.eclipse;

import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.internal.IHistoryListener;
import org.softwareFm.card.navigation.NavNextHistoryPrev;
import org.softwareFm.card.navigation.NavNextHistoryPrevConfig;
import org.softwareFm.display.browser.BrowserComposite;
import org.softwareFm.display.browser.IBrowserCompositeBuilder;
import org.softwareFm.display.browser.IBrowserPart;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwareFm.utilities.strings.Strings;

public class BrowserAndNavBar implements IBrowserCompositeBuilder {

	private final BrowserAndNavBarComposite content;

	static class TypeAndUrl {
		public final String type;
		public final String url;

		public TypeAndUrl(String type, String url) {
			this.type = type;
			this.url = url;
		}

		@Override
		public String toString() {
			return Strings.lastSegment(type, ".") + " - " + url;
		}
	}

	static class BrowserAndNavBarComposite extends Composite {
		BrowserComposite browser;
		Composite titleComposite;
		NavNextHistoryPrev<TypeAndUrl> navNextHistoryPrev;
		private final NavNextHistoryPrevConfig<TypeAndUrl> config;
		private final Label titleLabel;
		private final CardConfig cardConfig;

		public BrowserAndNavBarComposite(Composite parent, int style, final int margin, final CardConfig cardConfig, final NavNextHistoryPrevConfig<TypeAndUrl> config, IServiceExecutor service) {
			super(parent, style);
			this.cardConfig = cardConfig;
			this.config = config;
			titleComposite = new Composite(this, SWT.NULL);
			navNextHistoryPrev = new NavNextHistoryPrev<TypeAndUrl>(titleComposite, config);
			titleLabel = new Label(titleComposite, SWT.NULL);
			browser = new BrowserComposite(this, SWT.NULL, service);
			addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					Rectangle ca = getClientArea();
					e.gc.drawRoundRectangle(ca.x, ca.y, ca.width, ca.height, cardConfig.cornerRadius, cardConfig.cornerRadius);
				}
			});
			navNextHistoryPrev.getHistory().addHistoryListener(new IHistoryListener<BrowserAndNavBar.TypeAndUrl>() {
				@Override
				public void changingTo(TypeAndUrl typeAndUrl) {
					titleLabel.setText(typeAndUrl.toString());
				}
			});

		}

		@Override
		public Rectangle getClientArea() {
			Rectangle ca = super.getClientArea();
			Rectangle result = new Rectangle(ca.x + cardConfig.leftMargin, ca.y + cardConfig.topMargin, ca.width - cardConfig.leftMargin - cardConfig.rightMargin, ca.height - cardConfig.topMargin - cardConfig.bottomMargin);
			return result;
		}

		@Override
		public void setLayout(Layout layout) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void layout(boolean changed) {
			Rectangle ca = getClientArea();
			Control navHistoryPrevControl = navNextHistoryPrev.getControl();
			Point navSize = navHistoryPrevControl.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			int titleHeight = navSize.y;
			
			int cornerRadiusComp = cardConfig.cornerRadiusComp;
			int twiceComp = 2*cornerRadiusComp;
			
			titleComposite.setBounds(ca.x + cornerRadiusComp, ca.y + cornerRadiusComp, ca.width - twiceComp, titleHeight);
			navHistoryPrevControl.setBounds(0, 0, navSize.x, titleHeight);
			titleLabel.setBounds(ca.x + navSize.x, ca.y, ca.width - navSize.x, titleHeight );
			navNextHistoryPrev.layout();
			browser.getControl().setBounds(ca.x +cornerRadiusComp, ca.y + titleHeight + cornerRadiusComp, ca.width-twiceComp, ca.height - titleHeight-twiceComp);
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			Point size = browser.getControl().computeSize(wHint, hHint == SWT.DEFAULT ? SWT.DEFAULT : hHint - config.height);
			return new Point(size.x, size.y + config.height);
		}
	}

	public BrowserAndNavBar(Composite parent, int style, int margin, CardConfig cardConfig, NavNextHistoryPrevConfig<TypeAndUrl> config, IServiceExecutor service) {
		content = new BrowserAndNavBarComposite(parent, style, margin, cardConfig, config, service);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Future<String> processUrl(String feedType, String url) {
		content.navNextHistoryPrev.visiting(new TypeAndUrl(feedType, url));
		return content.browser.processUrl(feedType, url);
	}

	@Override
	public IBrowserPart register(String feedType, IFunction1<Composite, IBrowserPart> displayCreator) {
		return content.browser.register(feedType, displayCreator);
	}

}

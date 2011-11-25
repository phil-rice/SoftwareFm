package org.softwarefm.collections.explorer;

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
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.navigation.internal.NavNextHistoryPrev;
import org.softwareFm.card.navigation.internal.NavNextHistoryPrevConfig;
import org.softwareFm.display.browser.BrowserComposite;
import org.softwareFm.display.browser.IBrowserCompositeBuilder;
import org.softwareFm.display.browser.IBrowserPart;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.timeline.PlayItem;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.history.IHistory;
import org.softwareFm.utilities.history.IHistoryListener;
import org.softwareFm.utilities.services.IServiceExecutor;

public class BrowserAndNavBar implements IBrowserCompositeBuilder {

	final BrowserAndNavBarComposite content;

	public static class BrowserAndNavBarLayout extends Layout {
		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			BrowserAndNavBarComposite nav = (BrowserAndNavBarComposite) composite;
			Point size = nav.browser.getControl().computeSize(wHint, hHint == SWT.DEFAULT ? SWT.DEFAULT : hHint - nav.config.height);
			return new Point(size.x, size.y + nav.config.height);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			BrowserAndNavBarComposite nav = (BrowserAndNavBarComposite) composite;
			Rectangle ca = nav.getClientArea();
			Control navHistoryPrevControl = nav.navNextHistoryPrev.getControl();
			Point navSize = navHistoryPrevControl.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			int titleHeight = navSize.y;

			int cornerRadiusComp = nav.cardConfig.cornerRadiusComp;
			int twiceComp = 2 * cornerRadiusComp;
			nav.titleComposite.setBounds(ca.x + cornerRadiusComp, ca.y + cornerRadiusComp, ca.width - twiceComp, titleHeight);
			navHistoryPrevControl.setBounds(0, 0, navSize.x, titleHeight);
			nav.titleLabel.setBounds(navSize.x, 0, ca.width - navSize.x, titleHeight);
			nav.navNextHistoryPrev.layout();
			nav.browser.getControl().setBounds(ca.x + cornerRadiusComp, ca.y + titleHeight + cornerRadiusComp, ca.width - twiceComp, ca.height - titleHeight - twiceComp);
			System.out.println("BANB " + Swts.boundsUpToShell(nav.navNextHistoryPrev.getControl()));
		}

	}

	static class BrowserAndNavBarComposite extends Composite {
		BrowserComposite browser;
		Composite titleComposite;
		NavNextHistoryPrev<PlayItem> navNextHistoryPrev;
		private final NavNextHistoryPrevConfig<PlayItem> config;
		private final Label titleLabel;
		private final CardConfig cardConfig;

		public BrowserAndNavBarComposite(Composite parent, int style, final int margin, final CardConfig cardConfig, final NavNextHistoryPrevConfig<PlayItem> config, IServiceExecutor service, IHistory<PlayItem> history) {
			super(parent, style);
			this.cardConfig = cardConfig;
			this.config = config;
			titleComposite = new Composite(this, SWT.NULL);
			navNextHistoryPrev = new NavNextHistoryPrev<PlayItem>(titleComposite, config, history);
			navNextHistoryPrev.setLayout(new NavNextHistoryPrev.NavNextHistoryPrevLayout<PlayItem>());
			navNextHistoryPrev.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));//issue-3
			titleLabel = new Label(titleComposite, SWT.NULL);
			browser = new BrowserComposite(this, SWT.NULL, service);
			addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					Rectangle ca = getClientArea();
					e.gc.drawRoundRectangle(ca.x - cardConfig.cornerRadiusComp, ca.y-cardConfig.cornerRadiusComp, ca.width, ca.height + 2 * cardConfig.cornerRadiusComp, cardConfig.cornerRadius, cardConfig.cornerRadius);
				}
			});
			history.addHistoryListener(new IHistoryListener<PlayItem>() {
				@Override
				public void changingTo(PlayItem playItem) {
					titleLabel.setText(playItem.toString());
					browser.processUrl(playItem.feedType, playItem.url);
				}
			});
		}

		@Override
		public Rectangle getClientArea() {
			Rectangle ca = super.getClientArea();
			Rectangle result = new Rectangle(ca.x + cardConfig.leftMargin, ca.y + cardConfig.topMargin, ca.width - cardConfig.leftMargin - cardConfig.rightMargin, ca.height - cardConfig.topMargin - cardConfig.bottomMargin);
			return result;
		}

	}

	public BrowserAndNavBar(Composite parent, int style, int margin, CardConfig cardConfig, NavNextHistoryPrevConfig<PlayItem> config, IServiceExecutor service, IHistory<PlayItem> history) {
		content = new BrowserAndNavBarComposite(parent, style, margin, cardConfig, config, service, history);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Future<String> processUrl(String feedType, String url) {
		content.navNextHistoryPrev.getHistory().push(new PlayItem(feedType, url));
		return content.browser.processUrl(feedType, url);
	}

	@Override
	public IBrowserPart register(String feedType, IFunction1<Composite, IBrowserPart> displayCreator) {
		return content.browser.register(feedType, displayCreator);
	}

	@Override
	public Composite getComposite() {
		return content;
	}

}

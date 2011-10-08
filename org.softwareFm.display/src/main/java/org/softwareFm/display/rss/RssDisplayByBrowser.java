package org.softwareFm.display.rss;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.browser.IBrowserPart;

public class RssDisplayByBrowser implements IBrowserPart {

	private final Browser browser;

	public RssDisplayByBrowser(Composite parent, int style) {
		browser = new Browser(parent, style);
	}

	@Override
	public Composite getComposite() {
		return browser;
	}

	@Override
	public Control getControl() {
		return browser;
	}

	@Override
	public boolean usesUrl() {
		return false;
	}

	@Override
	public void displayUrl(String url) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void displayReply(int statusCode, String reply) throws Exception {
		int index1 = reply.indexOf("<rss");
		int index2 = reply.indexOf("<feed");
		if (index1 == -1 && index2 == -1  )
			browser.setText("<h1>Not an RSS Feed</h1>");
		else{
			try {
				String html = new RssFeedTransformer().apply(reply);
				browser.setText(html);
			} catch (Exception e) {
				throw new RuntimeException("\n" + reply+"\n", e);
			}
		}

	}

}

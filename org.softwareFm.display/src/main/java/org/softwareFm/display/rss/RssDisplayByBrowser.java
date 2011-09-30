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
		int index = reply.indexOf("<rss");
		if (index == -1)
			browser.setText("<h1>Not an RSS Feed</h1>");
		else{
			String html = new RssFeedTransformer().apply(reply.substring(index));
			browser.setText(html);
		}

	}

}
package org.softwareFm.display.browser;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BrowserPart implements IBrowserPart{

	private final Browser browser;

	public BrowserPart(Composite parent, int style) {
		browser = new Browser(parent, style);
	}
	
	@Override
	public Control getControl() {
		return browser;
	}

	@Override
	public boolean usesUrl() {
		return true;
	}

	@Override
	public void displayUrl(String url) {
		browser.setUrl(url);
		
	}

	@Override
	public void displayReply(int statusCode, String reply) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public Composite getComposite() {
		return browser;
	}

}

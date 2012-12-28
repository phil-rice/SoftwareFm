package org.softwarefm.core.labelAndText;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;

abstract public class TextAndBrowserComposite extends TextAndControlComposite<Browser> {

	private String url;
	private Browser browser;

	public TextAndBrowserComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
	}

	@Override
	protected Browser makeComponent(SoftwareFmContainer<?> container, Composite parent) {
		return browser = new Browser(parent, SWT.NULL);
	}

	public void setUrl(String url) {
		this.url = url;
		this.browser.setUrl(url);
	}

	public String getUrl() {
		return url;
	}

	public void clearBrowser() {
		this.browser.setText("");
		this.url = "";
	}

	@Override
	public void notJavaElement(int selectionCount) {
		super.notJavaElement(selectionCount);
		clearBrowser();
	}

}

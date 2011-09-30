package org.softwareFm.eclipse.fixture;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.browser.IBrowserPart;

public class BrowserPartMock implements IBrowserPart {

	public int count;
	public final Composite from;
	public int statusCode;
	public String reply;
	public String url;

	private final Composite control;
	private final boolean usesUrl;

	public BrowserPartMock(Composite from, final String name, boolean usesUrl) {
		this.from = from;
		this.usesUrl = usesUrl;
		control = new Composite(from, SWT.NULL) {
			@Override
			public String toString() {
				return name + " " + super.toString() + "visible: " + isVisible();
			}
		};
	}

	@Override
	public void displayUrl(String url) {
		count++;
		this.url = url;

	}

	@Override
	public void displayReply(int statusCode, String reply) {
		this.statusCode = statusCode;
		this.reply = reply;
		count++;
	}

	@Override
	public String toString() {
		return "BrowserPartMock [count=" + count + ", from=" + from + ", statusCode=" + statusCode + ", reply=" + reply + "]";
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public boolean usesUrl() {
		return usesUrl;
	}

	@Override
	public Composite getComposite() {
		return control;
	}

}

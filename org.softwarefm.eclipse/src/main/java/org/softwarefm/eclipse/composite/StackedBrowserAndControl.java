package org.softwarefm.eclipse.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.swt.IHasControl;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.functions.Functions;
import org.softwarefm.utilities.functions.IFunction1;

/** A composite with StackLayout in which the browser can be showing, or some other composite */
public class StackedBrowserAndControl<C extends IHasControl> extends SoftwareFmComposite {
	private final Browser browser;
	private final C control;
	private final StackLayout layout;
	int layoutCount;
	String textOrUrl;

	public StackedBrowserAndControl(Composite parent, SoftwareFmContainer<?> container, IFunction1<Composite, C> creator) {
		super(parent, container);
		browser = new Browser(getComposite(), SWT.NULL){
			@Override
			protected void checkSubclass() {
			}
			@Override
			public boolean setUrl(String url) {
				log(StackedBrowserAndControl.class, "browser.setUrl{0}", url);
				return super.setUrl(url);
			}
			@Override
			public boolean setUrl(String url, String postData, String[] headers) {
				log(StackedBrowserAndControl.class, "browser.setUrl{0},{1},{2}", url, postData, Lists.list(headers));
				return super.setUrl(url, postData, headers);
			}
			@Override
			public boolean setText(String html) {
				log(StackedBrowserAndControl.class, "browser.setText{0}", html);
				return super.setText(html);
			}
			@Override
			public boolean setText(String html, boolean trusted) {
				log(StackedBrowserAndControl.class, "browser.setText{0},{1}", html, trusted);
				return super.setText(html, trusted);
			}
		};
		control = Functions.call(creator, getComposite());
		layout = new StackLayout();
		setLayout(layout);
	}

	@Override
	public void dispose() {
		super.dispose();
		control.getControl().dispose();
	}

	public void setText(String text) {
		log(this, "setText-cancelled({0})", text);
		this.textOrUrl = text;
		browser.setText(text);
		browserOnTop();
	}

	private void browserOnTop() {
		log(this, "browserOnTop");
		if (layout.topControl != browser) {
			layout.topControl = browser;
			layout();
		}
	}

	public void setUrlAndShow(String url) {
		log(this, "setUrlAndShow({0})", url);
		textOrUrl = url;
		browser.setUrl(url);
		browserOnTop();
	}

	/** This exists for tests */
	public String getTextOrUrl() {
		return textOrUrl;
	}

	public C showSecondaryControl() {
		log(this, "showSecondaryControl");
		layout.topControl = control.getControl();
		layout();
		return control;
	}

	public Control getTopControl() {
		return layout.topControl;
	}

	public C getSecondaryControl() {
		return control;
	}

	public Browser getBrowserForTest() {
		return browser;
	}

	@Override
	public void layout() {
		layoutCount++;
		super.layout();
	}
}

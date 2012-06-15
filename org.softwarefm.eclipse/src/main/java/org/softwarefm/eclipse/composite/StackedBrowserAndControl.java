package org.softwarefm.eclipse.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.swt.IHasControl;
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
		browser = new Browser(getComposite(), SWT.NULL);
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
		this.textOrUrl = text;
		browser.setText(text);
		browserOnTop();
	}

	private void browserOnTop() {
		if (layout.topControl != browser) {
			layout.topControl = browser;
			layout();
		}
	}

	public void setUrlAndShow(String url) {
		textOrUrl = url;
		browser.setUrl(url);
		browserOnTop();
	}

	/** This exists for tests */
	public String getTextOrUrl() {
		return textOrUrl;
	}

	public C showSecondaryControl() {
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

	public Browser getBrowser() {
		return browser;
	}

	@Override
	public void layout() {
		layoutCount++;
		super.layout();
	}
}

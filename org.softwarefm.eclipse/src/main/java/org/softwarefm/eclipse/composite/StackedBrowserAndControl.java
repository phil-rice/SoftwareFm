package org.softwarefm.eclipse.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.swt.HasComposite;
import org.softwarefm.eclipse.swt.IHasControl;
import org.softwarefm.utilities.functions.Functions;
import org.softwarefm.utilities.functions.IFunction1;

/** A composite with StackLayout in which the browser can be showing, or some other composite */
public class StackedBrowserAndControl<C extends IHasControl> extends HasComposite {
	private final Browser browser;
	private final C control;
	private final StackLayout layout;

	public StackedBrowserAndControl(Composite parent, IFunction1<Composite, C> creator) {
		super(parent);
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

	public void setUrlAndShow(String url) {
		browser.setUrl(url);
		layout.topControl = browser;
		layout();
	}

	public C showSecondaryControl() {
		layout.topControl = control.getControl();
		layout();
		return control;
	}

	public C getSecondaryControl() {
		return control;
	}

	public Browser getBrowser() {
		return browser;
	}
}

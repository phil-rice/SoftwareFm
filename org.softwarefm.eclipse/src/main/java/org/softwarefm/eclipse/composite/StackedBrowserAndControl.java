package org.softwarefm.eclipse.composite;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.browser.BrowserComposite;
import org.softwarefm.eclipse.swt.IHasControl;
import org.softwarefm.utilities.functions.Functions;
import org.softwarefm.utilities.functions.IFunction1;

/** A composite with StackLayout in which the browserComposite can be showing, or some other composite */
public class StackedBrowserAndControl<C extends IHasControl> extends SoftwareFmComposite {
	private final BrowserComposite browserComposite;
	private final C control;
	private final StackLayout layout;
	int layoutCount;
	String textOrUrl;

	public StackedBrowserAndControl(Composite parent, SoftwareFmContainer<?> container, IFunction1<Composite, C> creator) {
		super(parent, container);
		browserComposite = new BrowserComposite(getComposite(), container);
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
		log(this, "setText({0})", text);
		this.textOrUrl = text;
		// browserComposite.setText(text);
		browserOnTop();
	}

	private void browserOnTop() {
		log(this, "browserOnTop");
		if (layout.topControl != browserComposite.getComposite()) {
			layout.topControl = browserComposite.getComposite();
			layout();
		}
	}

	public void setUrlAndShow(String url) {
		log(this, "setUrlAndShow({0})", url);
		textOrUrl = url;
		browserComposite.setUrl(url);
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

	@Override
	public void layout() {
		layoutCount++;
		super.layout();
	}

	public BrowserComposite getBrowserForTest() {
		return browserComposite;
	}

}

package org.softwarefm.core.browser;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.swt.HasComposite;

public class BrowserHelper extends HasComposite {
	private final Browser browser;

	public BrowserHelper(Composite parent, ImageRegistry imageRegistry, Browser browser) {
		super(parent, imageRegistry);
		this.browser = browser;
	}

	public Browser getBrowser() {
		return browser;
	}

}

package org.softwarefm.core.browser;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.softwarefm.core.constants.ImageConstants;
import org.softwarefm.core.swt.Swts;

class BrowserToolbar extends BrowserHelper implements IUpdateButtonStatus{
	private ToolItem backButton;
	private ToolItem forwardButton;
	private ToolItem stopButton;
	private ToolItem refreshButton;

	public BrowserToolbar(Composite parent, Browser browser, ImageRegistry imageRegistry) {
		super(parent, imageRegistry, browser);
		browser.addLocationListener(new LocationListener() {
			public void changing(LocationEvent event) {
			}

			public void changed(LocationEvent event) {
				updateButtonStatus();
			}
		});
	}

	@Override
	protected Composite makeComposite(Composite parent, ImageRegistry imageRegistry) {
		ToolBar toolbar = new ToolBar(parent, SWT.NULL);
		backButton = Swts.createPushToolItem(toolbar, imageRegistry, ImageConstants.backButton, new Listener() {
			public void handleEvent(Event event) {
				getBrowser().back();
			}
		});
		forwardButton = Swts.createPushToolItem(toolbar, imageRegistry, ImageConstants.forwardButton, new Listener() {
			public void handleEvent(Event event) {
				getBrowser().forward();
			}
		});
		stopButton = Swts.createPushToolItem(toolbar, imageRegistry, ImageConstants.stopButton, new Listener() {
			public void handleEvent(Event event) {
				getBrowser().stop();
			}
		});
		refreshButton = Swts.createPushToolItem(toolbar, imageRegistry, ImageConstants.refreshButton, new Listener() {
			public void handleEvent(Event event) {
				getBrowser().refresh();
			}
		});
		return toolbar;
	}

	public void updateButtonStatus() {
		backButton.setEnabled(getBrowser().isBackEnabled());
		forwardButton.setEnabled(getBrowser().isForwardEnabled());
	}
}
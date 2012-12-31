package org.softwarefm.core.browser;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.swt.Swts;

class BrowserUrlCombo extends BrowserHelper {

	public BrowserUrlCombo(Composite parent, final Browser browser, final IUpdateButtonStatus updateButtonStatus) {
		super(parent, null, browser);
		final Combo urlCombo = (Combo) getComposite();
		browser.addLocationListener(new LocationListener() {
			public void changing(LocationEvent event) {
			}

			public void changed(LocationEvent event) {
				if (event.top) {
					Swts.addItemToStartOfCombo(urlCombo, event.location, 10);
				}
			}
		});
		urlCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getBrowser().setUrl(urlCombo.getText());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);

			}
		});
		urlCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtonStatus.updateButtonStatus();
			}
		});

	}

	@Override
	protected Composite makeComposite(Composite parent, ImageRegistry imageRegistry) {
		return new Combo(parent, SWT.DROP_DOWN);
	}
}

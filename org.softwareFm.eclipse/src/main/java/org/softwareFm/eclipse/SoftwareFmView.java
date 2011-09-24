package org.softwareFm.eclipse;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.display.SoftwareFmDataComposite;
import org.softwareFm.display.Swts;
import org.softwareFm.display.actions.Actions;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IGuiDataListener;
import org.softwareFm.utilities.callbacks.ICallback;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The sample creates a dummy model on the fly, but a real implementation would connect to the model available either in this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class SoftwareFmView extends ViewPart {

	private Browser browser;

	public SoftwareFmView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, true));
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();

		final SoftwareFmDataComposite dataComposite = activator.makeComposite(composite, new ICallback<String>() {
			@Override
			public void process(final String t) throws Exception {
				parent.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (!browser.isDisposed()) {
							browser.setText(t);
							System.out.println("browser.setText:\n" + t);
						}
					}
				});
			}
		});

		final GuiDataStore guiDataStore = activator.getGuiDataStore();
		browser = new Browser(composite, SWT.BORDER);
		guiDataStore.addGuiDataListener(new IGuiDataListener() {
			@Override
			public void data(String entity, String url) {
				if (entity.equals("project")) {
					if (!display(dataComposite, guiDataStore, "data.project.rss", "{0}"))
						if (!display(dataComposite, guiDataStore, "data.project.tweets", "http://mobile.twitter.com/{0}"))
							display(dataComposite, guiDataStore, "data.jar.project.url", "{0}");
				}
			}

			private boolean display(final SoftwareFmDataComposite dataComposite, final GuiDataStore guiDataStore, String pathOrKey, final String pattern) {
				Object data = guiDataStore.getDataFor(pathOrKey);
				final String url = Actions.getStringOrItemOfList(data, 0).trim();
				if (data != null) {
					if (url.length() > 0) {
						Swts.asyncExec(dataComposite, new Runnable() {
							@Override
							public void run() {
								String actualUrl = MessageFormat.format(pattern, url);
								System.out.println("Browser.setUrl(" + actualUrl + ")");
								browser.setUrl(actualUrl);
							}
						});
						return true;
					}
				}
				return false;
			}
		});
		dataComposite.getComposite().setLayoutData(Swts.makeGrabHorizonalAndFillGridData());
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

}
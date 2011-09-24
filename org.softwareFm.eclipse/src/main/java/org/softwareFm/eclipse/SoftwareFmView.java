package org.softwareFm.eclipse;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.display.SoftwareFmDataComposite;
import org.softwareFm.display.Swts;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IGuiDataListener;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.strings.Strings;

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
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, true));
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();

		final SoftwareFmDataComposite dataComposite = activator.makeComposite(composite, new ICallback<String>() {
			@Override
			public void process(String t) throws Exception {
				browser.setUrl(t);
			}
		});

		final GuiDataStore guiDataStore = activator.getGuiDataStore();
		browser = new Browser(composite, SWT.BORDER);
		guiDataStore.addGuiDataListener(new IGuiDataListener() {
			@Override
			public void data(String entity, String url) {
				if (entity.equals("project")) {
					if (!display(dataComposite, guiDataStore, "data.project.rss", "{0}"))
						display(dataComposite, guiDataStore, "data.project.tweets", "http://mobile.twitter.com/{0}");
				}
			}

			private boolean display(final SoftwareFmDataComposite dataComposite, final GuiDataStore guiDataStore, String pathOrKey, final String pattern) {
				Object list = guiDataStore.getDataFor(pathOrKey);
				if (list != null && list instanceof List) {
					final String firstItem = Strings.nullSafeToString(((List<Object>) list).get(0));
					if (firstItem.trim().length() > 0) {
						Swts.asyncExec(dataComposite, new Runnable() {
							@Override
							public void run() {
								String url = MessageFormat.format(pattern, firstItem);
								browser.setUrl(url);
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
package org.softwarefm.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.selection.SelectedBindingAdapter;
import org.softwarefm.utilities.constants.CommonConstants;

public class AllPanel extends SoftwareFmPanel {

	public AllPanel(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		getComposite().setLayout(new FillLayout());
		TabFolder tabFolder = new TabFolder(getComposite(), SWT.NULL);
		final Browser classAndMethodBrowser = new Browser(tabFolder, SWT.NULL);
		final SoftwareFmPanel digestPanel = new DigestPanel(tabFolder, container);
		final ProjectPanel projectPanel = new ProjectPanel(tabFolder, container);
		final SoftwareFmPanel versionPanel = new VersionPanel(tabFolder, container);

		addTabItem(tabFolder, "Class And method", classAndMethodBrowser);
		addTabItem(tabFolder, "Digest", digestPanel.getControl());
		addTabItem(tabFolder, "Project", projectPanel.getControl());
		addTabItem(tabFolder, "Version", versionPanel.getControl());

		addListener(new SelectedBindingAdapter() {
			@Override
			public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
				String packageAndClass = expressionData.packageName + "/" + expressionData.className;
				String rl = expressionData.methodName == null ? packageAndClass : packageAndClass + "/" + expressionData.methodName;
				classAndMethodBrowser.setUrl(CommonConstants.softwareFmHostAndPrefix + "java/" + rl);
			}
		});
	}

	private void addTabItem(TabFolder tabFolder, String title, Control control) {
		TabItem tabItem = new TabItem(tabFolder, SWT.BORDER);
		tabItem.setText(title);
		tabItem.setControl(control);
	}

}

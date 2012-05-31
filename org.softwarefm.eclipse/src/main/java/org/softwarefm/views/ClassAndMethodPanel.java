package org.softwarefm.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.selection.SelectedBindingAdapter;
import org.softwarefm.utilities.constants.CommonConstants;

public class ClassAndMethodPanel extends SoftwareFmPanel {

	private final Browser browser;
	private String url;

	public ClassAndMethodPanel(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		getComposite().setLayout(new FillLayout());
		browser = new Browser(getComposite(), SWT.NULL);
		addListener(new SelectedBindingAdapter() {

			@Override
			public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
				String packageAndClass = expressionData.packageName + "/" + expressionData.className;
				String rl = expressionData.methodName == null ? packageAndClass : packageAndClass + "/" + expressionData.methodName;
				url = CommonConstants.softwareFmHostAndPrefix + "java/" + rl;
				browser.setUrl(url);
			}

		});
	}

	public String getUrl() {
		return url;
	}
}

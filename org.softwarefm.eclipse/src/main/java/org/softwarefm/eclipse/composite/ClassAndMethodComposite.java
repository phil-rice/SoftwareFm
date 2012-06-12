package org.softwarefm.eclipse.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.selection.SelectedBindingAdapter;

public class ClassAndMethodComposite extends SoftwareFmComposite {

	private final Browser browser;
	private String url;

	public ClassAndMethodComposite(Composite parent, final SoftwareFmContainer<?> container) {
		super(parent, container);
		getComposite().setLayout(new FillLayout());
		browser = new Browser(getComposite(), SWT.NULL);
		addListener(new SelectedBindingAdapter() {
			@Override
			public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
				url = container.urlStrategy.classAndMethodUrl(expressionData).getHostAndUrl();
				System.out.println("ClassAndMethod: " + url);
				browser.setUrl(url);
			}

		});
	}

	public String getUrl() {
		return url;
	}
}

package org.softwareFm.card.navigation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;

public class NavTitle implements IHasUrl {

	private final Label label;
	private final IFunction1<String, String> urlToTitle;

	public NavTitle(Composite parent, String initialLabel, IFunction1<String, String> urlToTitle) {
		this.urlToTitle = urlToTitle;
		label = new Label(parent, SWT.NULL);
		label.setText(initialLabel);
	}

	@Override
	public void setUrl(String url) {
		label.setText(Functions.call(urlToTitle,url));
	}

}

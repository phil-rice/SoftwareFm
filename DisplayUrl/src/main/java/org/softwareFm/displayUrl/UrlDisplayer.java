package org.softwareFm.displayUrl;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithLabel;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.BoundTitleAndTextField;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.Displayers;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.swtBasics.images.ImageButtons;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.strings.Strings;

public class UrlDisplayer extends AbstractDisplayerWithLabel<BoundTitleAndTextField> {

	@Override
	public BoundTitleAndTextField createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		final BoundTitleAndTextField boundTitleAndTextField = new BoundTitleAndTextField(parent, context, displayerDetails);
		boundTitleAndTextField.addEditButton();
		ImageButtons.addBrowseButton(boundTitleAndTextField, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return boundTitleAndTextField.getText();
			}
		});
		ImageButtons.addHelpButton(boundTitleAndTextField, displayerDetails.key);

		return boundTitleAndTextField;
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, BoundTitleAndTextField largeControl, Object value) {
		largeControl.setUrl(bindingContext.url);
		largeControl.setText(Strings.nullSafeToString(value));
	}

	public static void main(String[] args) {
		Displayers.displayWithKey1(new UrlDisplayer(), Resources.resourceGetterWithBasics("DisplayForTest"), "www.google.com");
	}

}
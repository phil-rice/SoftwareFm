package org.arc4eclipse.displayUrl;

import java.util.concurrent.Callable;

import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.Displayers;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.swtBasics.images.ImageButtons;
import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.widgets.Composite;

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
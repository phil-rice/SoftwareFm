package org.arc4eclipse.displayUrl;

import java.awt.Desktop;
import java.net.URI;

import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.strings.Strings;
import org.arc4eclipse.utilities.strings.Urls;
import org.eclipse.swt.widgets.Composite;

public class UrlDisplayer extends AbstractDisplayerWithLabel<BoundTitleAndTextField> {

	@Override
	public BoundTitleAndTextField createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		final BoundTitleAndTextField boundTitleAndTextField = new BoundTitleAndTextField(parent, context, displayerDetails);
		boundTitleAndTextField.addButton(DisplayUrlConstants.browse, displayerDetails.key, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				try {
					URI uri = Urls.withDefaultProtocol("http", boundTitleAndTextField.getText());
					Desktop.getDesktop().browse(uri);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
		return boundTitleAndTextField;
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, BoundTitleAndTextField largeControl, Object value) {
		largeControl.setUrl(bindingContext.url);
		largeControl.setText(Strings.nullSafeToString(value));
	}

}
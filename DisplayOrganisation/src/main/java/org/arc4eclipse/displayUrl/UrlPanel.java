package org.arc4eclipse.displayUrl;

import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class UrlPanel extends Composite {

	private final BoundTitleAndTextField orgField;
	private final ImageButton btnBrowse;

	public UrlPanel(Composite parent, int style, DisplayerContext context, DisplayerDetails details) {
		super(parent, style);
		orgField = new BoundTitleAndTextField(this, SWT.NULL, context, details);
		btnBrowse = orgField.addButton(context.imageFactory.makeImages(getDisplay()).getBrowseImage(), "Browse", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				System.out.println("Browse");
			}
		});
		orgField.addHelpButton(details.help);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);

	}

	public void setValue(String url, String value) {
		orgField.setUrl(url);
		orgField.setText(value);
		ImageButton.Utils.setEnabledIfNotBlank(btnBrowse, value);

	}

}

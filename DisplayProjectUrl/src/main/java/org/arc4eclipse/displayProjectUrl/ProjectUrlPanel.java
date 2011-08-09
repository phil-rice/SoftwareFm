package org.arc4eclipse.displayProjectUrl;

import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ProjectUrlPanel extends Composite {

	private final BoundTitleAndTextField urlField;
	private final ImageButton btnBrowse;

	public ProjectUrlPanel(Composite parent, int style, DisplayerContext context, String entity, NameSpaceAndName nameSpaceAndName, String title) {
		super(parent, style);
		urlField = new BoundTitleAndTextField(this, SWT.NULL, context, entity, nameSpaceAndName, title);
		btnBrowse = urlField.addButton(context.imageFactory.makeImages(getDisplay()).getBrowseImage(), "Browse", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				System.out.println("Browse");
			}
		});
		urlField.addHelpButton(DisplayProjectUrlConstants.helpProjectUrl);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);

	}

	public void setValue(String url, String value) {
		urlField.setUrl(url);
		urlField.setText(value);
		ImageButton.Utils.setEnabledIfNotBlank(btnBrowse, value);

	}

}

package org.arc4eclipse.displayOrganisationUrl;

import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class OrganisationPanel extends Composite {

	private final BoundTitleAndTextField orgField;
	private final ImageButton btnBrowse;

	public OrganisationPanel(Composite parent, int style, DisplayerContext context, String entity, NameSpaceAndName nameSpaceAndName, String title) {
		super(parent, style);
		orgField = new BoundTitleAndTextField(this, SWT.NULL, context, entity, nameSpaceAndName, title);
		btnBrowse = orgField.addButton(context.imageFactory.makeImages(getDisplay()).getBrowseImage(), "Browse", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				System.out.println("Browse");
			}
		});
		orgField.addHelpButton(DisplayOrganisationUrlConstants.helpOrganisationUrl);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);

	}

	public void setValue(String url, String value) {
		orgField.setUrl(url);
		orgField.setText(value);
		btnBrowse.setEnabled(!orgField.getText().equals(""));

	}

}

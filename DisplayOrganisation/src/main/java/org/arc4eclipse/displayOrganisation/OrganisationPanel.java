package org.arc4eclipse.displayOrganisation;

import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class OrganisationPanel extends Composite {

	private final BoundTitleAndTextField orgField;
	private final ImageButton btnBrowse;

	public OrganisationPanel(Composite parent, int style, DisplayerContext context, NameSpaceAndName nameSpaceAndName, String title) {
		super(parent, style);
		setLayout(new GridLayout());
		orgField = new BoundTitleAndTextField(this, context, nameSpaceAndName, title);
		btnBrowse = orgField.addButton(context.imageFactory.makeImages(getDisplay()).getBrowseImage(), "Browse", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				System.out.println("Browse");
			}
		});
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);

	}

	public void setValue(String url, String value) {
		orgField.setUrl(url);
		orgField.setText(value);
		btnBrowse.setEnabled(!orgField.getText().equals(""));

	}

}

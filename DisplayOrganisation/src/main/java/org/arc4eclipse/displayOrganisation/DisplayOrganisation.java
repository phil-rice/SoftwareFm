package org.arc4eclipse.displayOrganisation;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayOrganisation implements IDisplayer {

	@Override
	public String getNameSpace() {
		return "organisation";
	}

	@Override
	public Control makeCompositeAsChildOf(Composite parent, final BindingContext bindingContext, final NameSpaceNameAndValue nameSpaceNameAndValue) {
		BoundTitleAndTextField textField = new BoundTitleAndTextField(parent, bindingContext, nameSpaceNameAndValue);
		ImageButton btnBrowse = textField.addButton(bindingContext.images.getBrowseImage(), "Browse", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				System.out.println("Browse");
			}
		});
		btnBrowse.setEnabled(!textField.getText().equals(""));
		return textField;
	}
}
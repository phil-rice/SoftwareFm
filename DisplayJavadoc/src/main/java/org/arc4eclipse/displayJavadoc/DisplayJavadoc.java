package org.arc4eclipse.displayJavadoc;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayJavadoc implements IDisplayer {

	@Override
	public String getNameSpace() {
		return RepositoryConstants.javadocKey;
	}

	@Override
	public Control makeCompositeAsChildOf(Composite parent, final BindingContext bindingContext, final NameSpaceNameAndValue nameSpaceNameAndValue) {
		final TitleAndTextField textField = new BoundTitleAndTextField(parent, bindingContext, nameSpaceNameAndValue);
		ImageButton btnAttach = textField.addButton(bindingContext.images.getLinkImage(), "Attach", new IImageButtonListener() {

			@Override
			public void buttonPressed(ImageButton button) {
				System.out.println("Attaching javadoc");
			}
		});
		btnAttach.setEnabled(!textField.getText().equals(""));
		return textField;
	}
}
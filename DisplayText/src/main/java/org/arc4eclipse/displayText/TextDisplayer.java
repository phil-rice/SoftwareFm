package org.arc4eclipse.displayText;

import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.Displayers;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.swtBasics.images.ImageButtons;
import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.widgets.Composite;

public class TextDisplayer extends AbstractDisplayerWithLabel<BoundTitleAndTextField> {

	@Override
	public BoundTitleAndTextField createLargeControl(DisplayerContext context, final IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		BoundTitleAndTextField boundTitleAndTextField = new BoundTitleAndTextField(parent, context, displayerDetails);
		final String editorName = displayerDetails.map.get(DisplayCoreConstants.editor);
		if (editorName != null)
			boundTitleAndTextField.addEditButton();
		// ImageButtons.addEditButton(boundTitleAndTextField, new IImageButtonListener() {
		// @Override
		// public void buttonPressed(ImageButton button) {
		// IEditor editor = registeredItems.getEditor(editorName);
		// System.out.println("Found editor for " + editorName + ": " + editor);
		// }
		// });
		ImageButtons.addHelpButton(boundTitleAndTextField, displayerDetails.key);
		return boundTitleAndTextField;
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, BoundTitleAndTextField largeControl, Object value) {
		largeControl.setUrl(bindingContext.url);
		largeControl.setText(Strings.nullSafeToString(value));
	}

	@Override
	public String toString() {
		return "TextDisplayer";
	}

	public static void main(String[] args) {
		Displayers.displayWithKey1(new TextDisplayer(), Resources.resourceGetterWithBasics("DisplayForTest"), "text");
	}
}
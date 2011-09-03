package org.softwareFm.displayText;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithLabel;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.BoundTitleAndTextField;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.Displayers;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.swtBasics.images.ImageButtons;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.strings.Strings;

public class TextDisplayer extends AbstractDisplayerWithLabel<BoundTitleAndTextField> {

	@Override
	public BoundTitleAndTextField createLargeControl(DisplayerContext context, final IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		BoundTitleAndTextField boundTitleAndTextField = new BoundTitleAndTextField(parent, context, displayerDetails);
		final String editorName = displayerDetails.map.get(DisplayCoreConstants.editor);
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
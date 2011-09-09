package org.softwareFm.displayComposite;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithLabel;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.BoundTitleAndTextField;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.Displayers;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.strings.Strings;

public class CompositeDisplayer extends AbstractDisplayerWithLabel<BoundTitleAndTextField> {

	@Override
	public BoundTitleAndTextField createLargeControl(DisplayerContext context, final IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		BoundTitleAndTextField boundTitleAndTextField = new BoundTitleAndTextField(parent, context, displayerDetails);
		ImageButtons.addEditButton(boundTitleAndTextField, displayerDetails.getSmallImageKey(), OverlaysAnchor.editKey, boundTitleAndTextField.editButtonListener());
		ImageButtons.addHelpButton(boundTitleAndTextField, displayerDetails.key, GeneralAnchor.helpKey);
		return boundTitleAndTextField;
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, BoundTitleAndTextField largeControl, Object value) {
		largeControl.setLastBindingContext(bindingContext);
		largeControl.setText(Strings.nullSafeToString(value));
	}

	@Override
	public String toString() {
		return "CompositeDisplayer";
	}

	public static void main(String[] args) {
		Displayers.displayWithKey1(new CompositeDisplayer(), Resources.resourceGetterWithBasics("org.softwareFm.displayCore.api.DisplayForTest"), "text");
	}
}
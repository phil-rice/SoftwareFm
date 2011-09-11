package org.softwareFm.displayText;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithSummaryIcon;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.BoundTitleAndTextField;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.Displayers;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.displayCore.api.SummaryIcon;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.strings.Strings;

public class TextDisplayer extends AbstractDisplayerWithSummaryIcon<BoundTitleAndTextField, SummaryIcon> {

	@Override
	public BoundTitleAndTextField createLargeControl(DisplayerContext context, final IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		BoundTitleAndTextField boundTitleAndTextField = new BoundTitleAndTextField(parent, context, displayerDetails);
		ImageButtons.addEditButton(boundTitleAndTextField, displayerDetails.getSmallImageKey(), OverlaysAnchor.editKey, boundTitleAndTextField.editButtonListener());
		ImageButtons.addHelpButton(boundTitleAndTextField, displayerDetails.key, GeneralAnchor.helpKey);
		return boundTitleAndTextField;
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, BoundTitleAndTextField largeControl, Object value) {
		if (RepositoryDataItemStatus.Utils.isResults(bindingContext.status) && IDisplayer.Utils.entitiesMatch(bindingContext, largeControl.getEntity())) {
			largeControl.setLastBindingContext(bindingContext);
			largeControl.setText(Strings.nullSafeToString(value));
		}
	}

	@Override
	public String toString() {
		return "TextDisplayer";
	}

	public static void main(String[] args) {
		Displayers.displayWithKey1(new TextDisplayer(), Resources.resourceGetterWithBasics("org.softwareFm.displayCore.api.DisplayForTest"), "text");
	}
}
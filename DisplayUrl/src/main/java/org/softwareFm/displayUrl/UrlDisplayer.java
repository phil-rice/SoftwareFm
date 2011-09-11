package org.softwareFm.displayUrl;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithSummaryIcon;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.BoundTitleAndTextField;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.Displayers;
import org.softwareFm.displayCore.api.IDisplayContainerFactoryGetter;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.displayCore.api.SummaryIcon;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.strings.Strings;

public class UrlDisplayer extends AbstractDisplayerWithSummaryIcon<BoundTitleAndTextField, SummaryIcon> {

	@Override
	public BoundTitleAndTextField createLargeControl(Composite parent, DisplayerContext context, IRegisteredItems registeredItems, IDisplayContainerFactoryGetter displayContainerFactoryGetter, DisplayerDetails displayerDetails) {
		final BoundTitleAndTextField boundTitleAndTextField = new BoundTitleAndTextField(parent, context, displayerDetails);
		ImageButtons.addEditButton(boundTitleAndTextField, displayerDetails.getSmallImageKey(), OverlaysAnchor.editKey, boundTitleAndTextField.editButtonListener());
		ImageButtons.addBrowseButton(boundTitleAndTextField, GeneralAnchor.browseKey, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return boundTitleAndTextField.getText();
			}
		});
		ImageButtons.addHelpButton(boundTitleAndTextField, displayerDetails.key, GeneralAnchor.helpKey);

		return boundTitleAndTextField;
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, BoundTitleAndTextField largeControl, Object value) {
		if (RepositoryDataItemStatus.Utils.isResults(bindingContext.status)) {
			largeControl.setLastBindingContext(bindingContext);
			largeControl.setText(Strings.nullSafeToString(value));
		}
	}

	public static void main(String[] args) {
		Displayers.displayWithKey1(new UrlDisplayer(), Resources.resourceGetterWithBasics("org.softwareFm.displayCore.api.DisplayForTest"), "www.google.com");
	}

}
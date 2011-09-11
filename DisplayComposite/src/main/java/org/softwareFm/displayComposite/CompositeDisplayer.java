package org.softwareFm.displayComposite;

import java.text.MessageFormat;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithSummaryIcon;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.Displayers;
import org.softwareFm.displayCore.api.IDisplayContainer;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;
import org.softwareFm.displayCore.api.IDisplayContainerFactoryGetter;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.displayCore.api.SummaryIcon;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.swtBasics.images.Resources;

public class CompositeDisplayer extends AbstractDisplayerWithSummaryIcon<IDisplayContainer, SummaryIcon> {

	@Override
	public IDisplayContainer createLargeControl(Composite parent, DisplayerContext displayerContext, final IRegisteredItems registeredItems, IDisplayContainerFactoryGetter displayContainerFactoryGetter, DisplayerDetails displayerDetails) {
		String view = displayerDetails.map.get(DisplayCoreConstants.viewKey);
		if (view == null)
			throw new NullPointerException(MessageFormat.format(DisplayCoreConstants.missingValueInMap, DisplayCoreConstants.viewKey, displayerDetails.map));
		IDisplayContainerFactory factory = displayContainerFactoryGetter.getDisplayContainerFactory(parent.getDisplay(), view);
		IDisplayContainer displayContainer = factory.create(displayerContext, parent);
		return displayContainer;
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, IDisplayContainer largeControl, Object value) {
		largeControl.setValues(bindingContext);
	}

	@Override
	public String toString() {
		return "CompositeDisplayer";
	}

	public static void main(String[] args) {
		Displayers.displayWithKey1(new CompositeDisplayer(), Resources.resourceGetterWithBasics("org.softwareFm.displayCore.api.DisplayForTest"), "text");
	}
}
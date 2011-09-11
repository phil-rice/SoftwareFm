package org.softwareFm.displayLists;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithSummaryIcon;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.IDisplayContainerFactoryGetter;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.displayCore.api.SummaryIcon;
import org.softwareFm.repository.api.RepositoryDataItemStatus;

public class ListDisplayer extends AbstractDisplayerWithSummaryIcon<ListPanel<?>, SummaryIcon> {

	@Override
	public ListPanel<?> createLargeControl(Composite parent, DisplayerContext context, IRegisteredItems registeredItems, IDisplayContainerFactoryGetter displayContainerFactoryGetter, DisplayerDetails displayerDetails) {
		return new ListPanel<Object>(parent, SWT.BORDER, context, displayerDetails, registeredItems);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, ListPanel<?> largeControl, Object value) {
		if (RepositoryDataItemStatus.Utils.isResults(bindingContext.status))
			largeControl.setValue(bindingContext, value);
	}

}
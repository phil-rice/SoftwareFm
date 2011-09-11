package org.softwareFm.displayJarPath;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithSummaryIcon;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.IDisplayContainerFactoryGetter;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.displayJavadocAndSource.JarSummaryImageButton;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.swtBasics.images.SmallIconPosition;

public class JarPathDisplayer extends AbstractDisplayerWithSummaryIcon<DisplayJarSummaryPanel, JarSummaryImageButton> {

	@Override
	public DisplayJarSummaryPanel createLargeControl(Composite parent, DisplayerContext context, IRegisteredItems registeredItems, IDisplayContainerFactoryGetter displayContainerFactoryGetter, DisplayerDetails displayerDetails) {
		return new DisplayJarSummaryPanel(parent, SWT.BORDER, context, displayerDetails);
	}

	@Override
	protected JarSummaryImageButton makeSummaryIcon(DisplayerContext displayerContext, Composite parent, DisplayerDetails displayerDetails) {
		return new JarSummaryImageButton(parent, displayerContext, displayerDetails, false, SmallIconPosition.allIcons);
	}

	@Override
	public void populateSmallControl(BindingContext bindingContext, JarSummaryImageButton smallControl, Object value) {
		super.populateSmallControl(bindingContext, smallControl, value);
		if (RepositoryDataItemStatus.Utils.isResults(bindingContext.status)) {
			JarSummaryImageButton button = smallControl;
			button.setSourceAndJavadocState(button.getSourceAndJavadocState().withBindingContext(bindingContext));
		}
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, DisplayJarSummaryPanel largeControl, Object value) {
		if (RepositoryDataItemStatus.Utils.isResults(bindingContext.status)) {
			largeControl.setValue(bindingContext);
		}
	}

}
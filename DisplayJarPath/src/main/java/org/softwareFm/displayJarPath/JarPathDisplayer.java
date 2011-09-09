package org.softwareFm.displayJarPath;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.displayCore.api.AbstractDisplayerWithLabel;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.displayJavadocAndSource.JarSummaryImageButton;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.SmallIconPosition;

public class JarPathDisplayer extends AbstractDisplayerWithLabel<DisplayJarSummaryPanel> {

	@Override
	public DisplayJarSummaryPanel createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		return new DisplayJarSummaryPanel(parent, SWT.BORDER, context, displayerDetails);
	}

	@Override
	protected ImageButton makeImageButton(DisplayerContext displayerContext, Composite parent, String smallImageKey) {
		return new JarSummaryImageButton(parent, displayerContext.imageRegistry, displayerContext.resourceGetter, smallImageKey, SmallIconPosition.allIcons, true);
	}

	@Override
	public void populateSmallControl(BindingContext bindingContext, Control smallControl, Object value) {
		super.populateSmallControl(bindingContext, smallControl, value);
		if (RepositoryDataItemStatus.Utils.isResults(bindingContext.status)) {
			JarSummaryImageButton button = (JarSummaryImageButton) smallControl;
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
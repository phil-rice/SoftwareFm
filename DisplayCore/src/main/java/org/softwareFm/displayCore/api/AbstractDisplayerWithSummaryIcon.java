package org.softwareFm.displayCore.api;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.Resources;

public abstract class AbstractDisplayerWithSummaryIcon<L extends Control, S extends SummaryIcon> implements IDisplayer<L, S> {

	@Override
	public S createSmallControl(DisplayerContext displayerContext, IRegisteredItems registeredItems, final ITopButtonState topButtonState, Composite parent, final DisplayerDetails displayerDetails) {
		final String key = displayerDetails.key;
		S button = makeSummaryIcon(displayerContext, parent, displayerDetails);
		button.setTooltipText(Resources.getTooltip(displayerContext.resourceGetter, key));
		button.addListener(new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				topButtonState.toogleState(key);
			}
		});
		return button;
	}

	@SuppressWarnings("unchecked")
	protected S makeSummaryIcon(DisplayerContext displayerContext, Composite parent, final DisplayerDetails displayerDetails) {
		return (S) new SummaryIcon(parent, displayerContext.imageRegistry, displayerDetails, false);
	}

	@Override
	public void populateSmallControl(BindingContext bindingContext, SummaryIcon smallControl, Object value) {
	}

	protected BindingRipperResult getBindingRipperResult(BindingContext bindingContext) {
		return (BindingRipperResult) bindingContext.context.get(DisplayCoreConstants.ripperResult);
	}

}
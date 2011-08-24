package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.images.Resources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractDisplayerWithLabel<L extends Control> implements IDisplayer<L, Control> {

	@Override
	public Control createSmallControl(DisplayerContext displayerContext, IRegisteredItems registeredItems, final ITopButtonState topButtonState, Composite parent, final DisplayerDetails displayerDetails) {
		final String key = displayerDetails.key;
		String smallImageKey = displayerDetails.map.get(DisplayCoreConstants.smallImageKey);
		ImageButton button = new ImageButton(parent, displayerContext.imageRegistry, smallImageKey, true);
		button.setTooltipText(Resources.getTooltip(displayerContext.resourceGetter, key));
		button.addListener(new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				topButtonState.toogleState(key);
			}
		});
		return button.getControl();
	}

	@Override
	public void populateSmallControl(BindingContext bindingContext, Control smallControl, Object value) {
	}

	protected BindingRipperResult getBindingRipperResult(BindingContext bindingContext) {
		return (BindingRipperResult) bindingContext.context.get(DisplayCoreConstants.ripperResult);
	}

}
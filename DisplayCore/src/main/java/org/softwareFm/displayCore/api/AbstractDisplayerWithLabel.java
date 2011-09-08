package org.softwareFm.displayCore.api;

import java.text.MessageFormat;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.Resources;

public abstract class AbstractDisplayerWithLabel<L extends Control> implements IDisplayer<L, Control> {

	@Override
	public Control createSmallControl(DisplayerContext displayerContext, IRegisteredItems registeredItems, final ITopButtonState topButtonState, Composite parent, final DisplayerDetails displayerDetails) {
		final String key = displayerDetails.key;
		String smallImageKey = displayerDetails.getSmallImageKey();
		if (smallImageKey == null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.smallImageKeyMissing, key, displayerDetails.map));
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
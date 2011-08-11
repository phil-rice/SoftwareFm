package org.arc4eclipse.displayCore.api;

import java.io.IOException;
import java.io.InputStream;

import org.arc4eclipse.displayCore.api.impl.ITopButtonState;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.springframework.core.io.ClassPathResource;

public abstract class AbstractDisplayerWithLabel<L extends Control> implements IDisplayer<L, Control> {
	abstract protected Image createMainImage(Device device);

	abstract protected Image createDepressedImage(Device device);

	private Image mainImage;
	private Image depressedImage;

	@Override
	public void dispose() {
		if (mainImage != null)
			mainImage.dispose();
		if (depressedImage != null)
			depressedImage.dispose();
	}

	@Override
	public Control createSmallControl(DisplayerContext displayerContext, final ITopButtonState topButtonState, Composite parent, final DisplayerDetails displayerDetails) {
		Device device = parent.getDisplay();
		ImageButton button = new ImageButton(parent, getMainImage(displayerDetails, device), getDepressedImage(displayerDetails, device));
		button.setTooltipText(displayerDetails.title);
		button.addListener(new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				topButtonState.toogleState(displayerDetails.nameSpaceAndName);
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

	protected Image getMainImage(DisplayerDetails displayerDetails, Device device) {
		if (displayerDetails.optionalImage != null)
			return displayerDetails.optionalImage;
		if (mainImage == null)
			mainImage = createMainImage(device);
		return mainImage;
	}

	protected Image getDepressedImage(DisplayerDetails displayerDetails, Device device) {
		if (displayerDetails.optionalImage != null)
			return displayerDetails.optionalImage;
		if (depressedImage == null)
			depressedImage = createDepressedImage(device);
		return depressedImage;
	}

	protected Image makeImage(Device device, String name) {
		try {
			InputStream inputStream = new ClassPathResource(name, getClass()).getInputStream();
			return new Image(device, inputStream);
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}

	}

}
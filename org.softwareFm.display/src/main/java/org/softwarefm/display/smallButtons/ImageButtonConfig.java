package org.softwarefm.display.smallButtons;

import java.text.MessageFormat;

import org.eclipse.jface.resource.ImageRegistry;
import org.softwarefm.display.SoftwareFmLayout;
import org.softwarefm.display.data.DisplayConstants;

public class ImageButtonConfig {
	public final SoftwareFmLayout layout;
	public final ImageRegistry imageRegistry;
	public final String depressedBackground;
	public final String normalBackground;
	public final String mainImage;
	public final String overlayImage;

	public ImageButtonConfig(SoftwareFmLayout layout, ImageRegistry imageRegistry, String depressedBackground, String normalBackground, String mainImage, String overlayImage) {
		this.layout = layout;
		this.imageRegistry = imageRegistry;
		this.depressedBackground = depressedBackground;
		this.normalBackground = normalBackground;
		this.mainImage = mainImage;
		this.overlayImage = overlayImage;
		checkImages(true, depressedBackground, normalBackground, mainImage, overlayImage);
	}

	
	public void validate(){
		checkImages(false, depressedBackground, normalBackground, mainImage);
	}
	private void checkImages(boolean allowNull, String... imageNames) {
		for (String image : imageNames)
			if (image != null)
				if (imageRegistry.get(image) == null)
					throw new IllegalStateException(MessageFormat.format(DisplayConstants.imageNotFound, image));

	}

	public ImageButtonConfig withImage(String mainImage) {
		return new ImageButtonConfig(layout, imageRegistry, depressedBackground, normalBackground, mainImage, null);
	}

	public ImageButtonConfig withImage(String mainImage, String overlayImage) {
		return new ImageButtonConfig(layout, imageRegistry, depressedBackground, normalBackground, mainImage, overlayImage);
	}
}

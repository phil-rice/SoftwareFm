package org.softwarefm.display;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.collections.Lists;
import org.softwarefm.display.data.DisplayConstants;
import org.softwarefm.display.smallButtons.IImageButtonListener;
import org.softwarefm.display.smallButtons.ImageButtonConfig;
import org.softwarefm.display.smallButtons.SimpleImageButton;

public class ActionDefn {

	public List<String> params;
	public String tooltip;
	public final String id;
	public final String mainImageId;
	public final String overlayId;

	public ActionDefn(String id, String mainImageId, String overlayId) {
		this.id = id;
		this.mainImageId = mainImageId;
		this.overlayId = overlayId;
		if (id == null)
			throw new NullPointerException(MessageFormat.format(DisplayConstants.mustHaveA, "id", id));
		if (mainImageId == null)
			throw new NullPointerException(MessageFormat.format(DisplayConstants.mustHaveA, "mainImageId", id));
	}

	public ActionDefn params(String... paramNames) {
		if (this.params != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "params", this.params, Arrays.asList(paramNames)));
		this.params = Lists.fromArray(paramNames);
		return this;
	}

	public ActionDefn tooltip(String tooltip) {
		if (this.tooltip != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "tooltip", this.tooltip, tooltip));
		this.tooltip = tooltip;
		return this;
	}

	public void validate() {
		if (tooltip == null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.mustHaveA, "tooltip", id));
		if (params == null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.mustHaveA, "params", id));
	}

	public IHasControl createButton(ImageButtonConfig config, IButtonParent buttonParent, IImageButtonListener imageButtonListener) {
		SimpleImageButton simpleImageButton = new SimpleImageButton(buttonParent, config.withImage(mainImageId, overlayId));
		if (tooltip != null)
			simpleImageButton.getControl().setToolTipText(Resources.getOrException(buttonParent.getResourceGetter(), tooltip));
		simpleImageButton.addListener(imageButtonListener);
		return simpleImageButton;
	}

}

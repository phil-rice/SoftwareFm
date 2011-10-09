package org.softwareFm.display.actions;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.smallButtons.IImageButtonListener;
import org.softwareFm.display.smallButtons.ImageButtonConfig;
import org.softwareFm.display.smallButtons.SimpleImageButton;
import org.softwareFm.utilities.collections.Lists;

public class ActionDefn {

	public List<String> params;
	public String tooltip;
	public final String id;
	public final String mainImageId;
	public final String overlayId;
	public boolean ignoreGuard;
	public boolean defaultAction;

	public ActionDefn(String id, String mainImageId, String overlayId) {
		this.id = id;
		this.mainImageId = mainImageId;
		this.overlayId = overlayId;
		if (id == null)
			throw new NullPointerException(MessageFormat.format(DisplayConstants.mustHaveA, "id", id));
		if (mainImageId == null)
			throw new NullPointerException(MessageFormat.format(DisplayConstants.mustHaveA, "mainImageId", id));
	}

	
	public ActionDefn ignoreGuard() {
		this.ignoreGuard = true;
		return this;
	}
	public ActionDefn thisIsDefault(){
		this.defaultAction = true;
		return this;
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
		SimpleImageButton simpleImageButton = new SimpleImageButton(buttonParent, config.withImage(mainImageId, overlayId), false);
		simpleImageButton.addListener(imageButtonListener);
		return simpleImageButton;
	}

	@Override
	public String toString() {
		return "ActionDefn [id=" + id + ", mainImageId=" + mainImageId + ", tooltip=" + tooltip + ", overlayId=" + overlayId + ", ignoreGuard=" + ignoreGuard + ", defaultAction=" + defaultAction + ", params=" + params + "]";
	}

}

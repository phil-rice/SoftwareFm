package org.softwarefm.display;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.softwareFm.utilities.collections.Lists;
import org.softwarefm.display.data.DisplayConstants;

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

}

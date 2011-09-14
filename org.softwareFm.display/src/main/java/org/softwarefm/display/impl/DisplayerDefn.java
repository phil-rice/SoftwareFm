package org.softwarefm.display.impl;

import java.text.MessageFormat;
import java.util.List;

import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;
import org.softwarefm.display.IAction;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.data.DisplayConstants;
import org.softwarefm.display.displayer.IDisplayer;

public class DisplayerDefn {

	@Override
	public String toString() {
		return "DisplayerDefn [displayer=" + displayer + ", defns=" + defns + ", actionStore=" + actionStore + ", dataKey=" + dataKey + ", tooltip=" + tooltip + "]";
	}

	public final IDisplayer displayer;
	public final List<ImageButtonDefn> defns = Lists.newList();
	private final ActionStore actionStore;

	public String dataKey;
	public String tooltip;

	public DisplayerDefn(IDisplayer displayer, ActionStore actionStore) {
		this.displayer = displayer;
		this.actionStore = actionStore;
	}

	public DisplayerDefn data(String dataKey) {
		if (this.dataKey != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "data", this.dataKey, dataKey));
		this.dataKey = dataKey;
		return this;
	}

	public DisplayerDefn tooltip(String tooltip) {
		if (this.tooltip != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "tooltip", this.tooltip, tooltip));
		this.tooltip = tooltip;
		return this;
	}

	public DisplayerDefn action(String actionId, String data, String mainImage) {
		IAction action = actionStore.get(actionId);
		defns.add(new ImageButtonDefn(mainImage, null, data, Maps.<SmallIconPosition, String> newMap(), action));
		return this;
	}
}

package org.softwarefm.display.impl;

import java.text.MessageFormat;
import java.util.List;

import org.softwareFm.utilities.collections.Lists;
import org.softwarefm.display.ActionDefn;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.data.DisplayConstants;
import org.softwarefm.display.displayer.IDisplayerFactory;

public class DisplayerDefn {

	@Override
	public String toString() {
		return "DisplayerDefn [displayer=" + displayer + ", defns=" + defns + ", actionStore=" + actionStore + ", dataKey=" + dataKey + ", title=" + title + ", tooltip=" + tooltip + "]";
	}

	public final IDisplayerFactory displayer;
	public final List<ImageButtonDefn> defns = Lists.newList();
	private final ActionStore actionStore;

	public String dataKey;
	public String title;
	public String tooltip;

	public DisplayerDefn(IDisplayerFactory displayer, ActionStore actionStore) {
		this.displayer = displayer;
		this.actionStore = actionStore;
	}

	public DisplayerDefn title(String title) {
		if (this.title != null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "title", this.title, title));
		this.title = title;
		return this;
		
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

//	public DisplayerDefn action(String actionId, String data, String mainImage) {
//		IAction action = actionStore.get(actionId);
//		defns.add(new ImageButtonDefn(mainImage, null, data, Maps.<SmallIconPosition, String> newMap(), action));
//		return this;
//	}

	public DisplayerDefn actions(ActionDefn ...actionDefns) {
		return this;
	}
}

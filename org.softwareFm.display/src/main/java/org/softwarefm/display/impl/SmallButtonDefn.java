package org.softwarefm.display.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.softwarefm.display.IAction;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.data.DisplayConstants;
import org.softwarefm.display.smallButtons.ISmallButtonFactory;

public class SmallButtonDefn {

	@Override
	public String toString() {
		return "SmallButtonDefn [actionStore=" + actionStore + ", smallButtonFactory=" + smallButtonFactory + ", tooltip=" + tooltip + ", id=" + id + ", defns=" + defns + ", controlClickAction=" + controlClickAction + ", controlClickActionData=" + controlClickActionData + "]";
	}

	private final ActionStore actionStore;
	public ISmallButtonFactory smallButtonFactory;

	public String tooltip;
	public String id;
	public final List<DisplayerDefn> defns;
	public IAction controlClickAction;
	public String controlClickActionData;

	public SmallButtonDefn(String id, ActionStore store, ISmallButtonFactory smallButtonFactory, DisplayerDefn... defns) {
		this.actionStore = store;
		this.id = id;
		this.smallButtonFactory = smallButtonFactory;
		this.defns = Collections.unmodifiableList(new ArrayList<DisplayerDefn>(Arrays.asList(defns)));
	}

	public SmallButtonDefn tooltip(String tooltip) {
		if (this.tooltip != null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "tooltip", this.tooltip, tooltip));
		this.tooltip = tooltip;
		return this;
	}

	public SmallButtonDefn ctrlClickAction(String actionId, String data) {
		controlClickAction = actionStore.get(actionId);
		controlClickActionData = data;
		return this;
	}
}

package org.softwareFm.display.smallButtons;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.actions.IAction;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.displayer.DisplayerDefn;

public class SmallButtonDefn {

	@Override
	public String toString() {
		return "SmallButtonDefn [id=" + id + ", dataId=" + dataId + ", titleId=" + titleId + ", tooltip=" + tooltip + ", defns=" + defns + ", smallButtonFactory=" + smallButtonFactory + ", controlClickAction=" + controlClickAction + ", controlClickActionData=" + controlClickActionData + ", mainImageId=" + mainImageId + ", actionStore=" + actionStore + "]";
	}

	private final ActionStore actionStore;
	public ISmallButtonFactory smallButtonFactory;

	public String tooltip;
	public String id;
	public String dataId;
	public final List<DisplayerDefn> defns;
	public IAction controlClickAction;
	public String controlClickActionData;
	public String mainImageId;
	public final String titleId;

	public SmallButtonDefn(String id, String titleId, String mainImageId, ActionStore store, ISmallButtonFactory smallButtonFactory, DisplayerDefn... defns) {
		this.titleId = titleId;
		this.mainImageId = mainImageId;
		this.actionStore = store;
		this.id = id;
		this.smallButtonFactory = smallButtonFactory;
		this.defns = Collections.unmodifiableList(new ArrayList<DisplayerDefn>(Arrays.asList(defns)));
	}

	public SmallButtonDefn data(String dataId) {
		if (this.dataId != null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "dataId", this.dataId, dataId));
		this.dataId = dataId;
		return this;

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

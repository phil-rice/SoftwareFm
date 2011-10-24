package org.softwareFm.display;

import org.softwareFm.display.actions.ActionDefn;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.DisplayerStore;
import org.softwareFm.display.displayer.IDisplayerFactory;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.display.smallButtons.ISmallButtonFactory;
import org.softwareFm.display.smallButtons.SmallButtonDefn;
import org.softwareFm.display.smallButtons.SmallButtonStore;

public class GuiBuilder {

	private final ActionStore actionStore;
	private final SmallButtonStore smallButtonStore;
	private final DisplayerStore displayerStore;

	public GuiBuilder(SmallButtonStore smallButtonStore, ActionStore actionStore, DisplayerStore displayerStore, ListEditorStore listEditorStore) {
		this.smallButtonStore = smallButtonStore;
		this.actionStore = actionStore;
		this.displayerStore = displayerStore;
	}

	public DisplayerDefn displayer(String displayerId) {
		IDisplayerFactory displayer = displayerStore.get(displayerId);
		return new DisplayerDefn(displayer);
	}

	public SmallButtonDefn smallButton(final String id, String titleId, String smallButtonId, String mainImageId, DisplayerDefn... defns) {
		ISmallButtonFactory smallButtonFactory = smallButtonStore.get(smallButtonId);
		return new SmallButtonDefn(id, titleId, mainImageId, actionStore, smallButtonFactory, defns);
	}

	public LargeButtonDefn largeButton(String string, SmallButtonDefn... defns) {
		return new LargeButtonDefn(string, defns);

	}

	public DisplayerDefn listDisplayer(String listDisplayId, String listEditorId) {
		IDisplayerFactory displayer = displayerStore.get(listDisplayId);
		return new DisplayerDefn(displayer, listEditorId);
	}

	public ActionDefn action(String actionId, String mainImageId) {
		return new ActionDefn(actionId, mainImageId, null);
	}

	public ActionDefn action(String actionId, String mainImageId, String overlayId) {
		return new ActionDefn(actionId, mainImageId, overlayId);
	}

	public ActionStore getActionStore() {
		return actionStore;
	}

	public DisplayerStore getDisplayerStore() {
		return displayerStore;
	}

	public SmallButtonStore getSmallButtonStore() {
		return smallButtonStore;
	}
}

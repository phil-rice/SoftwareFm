package org.softwareFm.display;

import org.eclipse.jface.resource.ImageRegistry;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.displayer.DisplayerStore;
import org.softwareFm.display.displayer.IDisplayerFactory;
import org.softwareFm.display.impl.DisplayerDefn;
import org.softwareFm.display.impl.LargeButtonDefn;
import org.softwareFm.display.impl.SmallButtonDefn;
import org.softwareFm.display.lists.IListEditor;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.display.smallButtons.ISmallButtonFactory;
import org.softwareFm.display.smallButtons.SmallButtonStore;
import org.softwareFm.utilities.resources.IResourceGetter;

public class GuiBuilder {

	private final ActionStore actionStore;
	private final SmallButtonStore smallButtonStore;
	private final DisplayerStore displayerStore;
	private final ListEditorStore listEditorStore;

	public GuiBuilder(IResourceGetter resourceGetter, ImageRegistry imageRegistry, SmallButtonStore smallButtonStore, GuiDataStore dataStore, ActionStore actionStore, DisplayerStore displayerStore, ListEditorStore listEditorStore) {
		this.smallButtonStore = smallButtonStore;
		this.actionStore = actionStore;
		this.displayerStore = displayerStore;
		this.listEditorStore = listEditorStore;
	}

	public DisplayerDefn displayer(String displayerId) {
		IDisplayerFactory displayer = displayerStore.get(displayerId);
		return new DisplayerDefn(displayer);
	}

	public SmallButtonDefn smallButton(final String id,String titleId, String smallButtonId, String mainImageId, DisplayerDefn... defns) {
		ISmallButtonFactory smallButtonFactory = smallButtonStore.get(smallButtonId);
		return new SmallButtonDefn(id, titleId, mainImageId, actionStore, smallButtonFactory, defns);
	}

	public LargeButtonDefn largeButton(String string, SmallButtonDefn... defns) {
		return new LargeButtonDefn(string, defns);

	}

	public DisplayerDefn listDisplayer(String listDisplayId, String listEditorId) {
		IDisplayerFactory displayer = displayerStore.get(listDisplayId);
		IListEditor listEditor = listEditorStore.get(listEditorId);
		return new DisplayerDefn(displayer, listEditor);
	}

	public ActionDefn action(String actionId, String mainImageId) {
		return new ActionDefn(actionId, mainImageId, null);
	}
	public ActionDefn action(String actionId, String mainImageId, String overlayId) {
		return new ActionDefn(actionId, mainImageId, overlayId);
	}

}

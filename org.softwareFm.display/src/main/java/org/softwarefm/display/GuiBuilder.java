package org.softwarefm.display;

import org.eclipse.jface.resource.ImageRegistry;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.data.GuiDataStore;
import org.softwarefm.display.displayer.DisplayerStore;
import org.softwarefm.display.displayer.IDisplayer;
import org.softwarefm.display.impl.DisplayerDefn;
import org.softwarefm.display.impl.LargeButtonDefn;
import org.softwarefm.display.impl.SmallButtonDefn;
import org.softwarefm.display.smallButtons.ISmallButtonFactory;
import org.softwarefm.display.smallButtons.SmallButtonStore;

public class GuiBuilder {

	private final ActionStore actionStore;
	private final SmallButtonStore smallButtonStore;
	private final DisplayerStore displayerStore;

	public GuiBuilder(IResourceGetter resourceGetter, ImageRegistry imageRegistry, SmallButtonStore smallButtonStore, GuiDataStore dataStore, ActionStore actionStore, DisplayerStore displayerStore) {
		this.smallButtonStore = smallButtonStore;
		this.actionStore = actionStore;
		this.displayerStore = displayerStore;
	}

	public DisplayerDefn displayer(String displayerId) {
		IDisplayer displayer = displayerStore.get(displayerId);
		return new DisplayerDefn(displayer, actionStore);
	}

	public SmallButtonDefn smallButton(final String id, String smallButtonId, String Id, DisplayerDefn... defns) {
		ISmallButtonFactory smallButtonFactory = smallButtonStore.get(smallButtonId);
		return new SmallButtonDefn(id, actionStore, smallButtonFactory, defns);
	}

	public LargeButtonDefn largeButton(String string, SmallButtonDefn... defns) {
		return new LargeButtonDefn(string, defns);

	}

	public DisplayerDefn listDisplayer(String listDisplayId, String lineEditorId) {
		IDisplayer displayer = displayerStore.get(listDisplayId);
		return new DisplayerDefn(displayer, actionStore);
	}

}

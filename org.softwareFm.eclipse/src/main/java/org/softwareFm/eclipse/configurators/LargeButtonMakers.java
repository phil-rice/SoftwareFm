package org.softwareFm.eclipse.configurators;

import org.softwareFm.display.ActionDefn;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;

public class LargeButtonMakers {
	public static ActionDefn[] makeMainListActions(GuiBuilder guiBuilder, String artifactId) {
		return new ActionDefn[] { guiBuilder.//
				action("action.list.add", artifactId, OverlaysAnchor.addKey).//
				tooltip("action.add.tooltip") };
	}

	public static ActionDefn listDeleteAction(GuiBuilder guiBuilder, String artifactId) {
		return guiBuilder.action("action.list.delete", artifactId, OverlaysAnchor.deleteKey).tooltip("action.delete.tooltip");
	}
}

package org.softwareFm.eclipse.plugin;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.eclipse.actions.IActionBar;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.explorer.IExplorer;
import org.softwareFm.swt.menu.ICardMenuItemHandler;

public class AdminView extends ExplorerView {

	@Override
	protected List<String> getRootUrls() {
		return Arrays.asList(CollectionConstants.rootUrl);
	}

	@Override
	protected void addMenuItems(IExplorer explorer) {
		ICardMenuItemHandler.Utils.addSoftwareFmMenuItemHandlers(explorer);
	}

	@Override
	protected IActionBar makeActionBar(IExplorer explorer, CardConfig cardConfig) {
		return IActionBar.Utils.actionBar(explorer, cardConfig, SelectedArtifactSelectionManager.reRipFn(), true);
	}
}

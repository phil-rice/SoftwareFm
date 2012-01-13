package org.softwareFm.explorer.eclipse;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.collections.actions.IActionBar;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.menu.ICardMenuItemHandler;

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

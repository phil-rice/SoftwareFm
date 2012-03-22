/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.plugin;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.eclipse.actions.IActionBar;
import org.softwareFm.jarAndClassPath.api.IUsageStrategy;
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
	protected IActionBar makeActionBar(IExplorer explorer, CardConfig cardConfig, IUsageStrategy usageStrategy) {
		return IActionBar.Utils.actionBar(explorer, cardConfig, SelectedArtifactSelectionManager.reRipFn(), true, usageStrategy);
	}
}
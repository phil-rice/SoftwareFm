/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.actions;

import org.eclipse.jface.action.IToolBarManager;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.eclipse.actions.internal.ActionBar;
import org.softwareFm.eclipse.jdtBinding.BindingRipperResult;
import org.softwareFm.eclipse.usage.IUsageStrategy;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.IExplorer;

public interface IActionBar {

	public void populateToolbar(IToolBarManager toolBarManager);

	public void selectionOccured(BindingRipperResult result);

	public static class Utils {
		public static IActionBar actionBar(IExplorer explorer, CardConfig cardConfig, IFunction1<BindingRipperResult, BindingRipperResult> reRipFn, boolean admin, IUsageStrategy usageStrategy) {
			return new ActionBar(explorer, cardConfig, reRipFn, admin);
		}
	}

}
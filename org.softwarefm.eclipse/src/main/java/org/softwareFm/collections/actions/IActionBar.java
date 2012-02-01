package org.softwareFm.collections.actions;

import org.eclipse.jface.action.IToolBarManager;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.collections.actions.internal.ActionBar;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.utilities.functions.IFunction1;

public interface IActionBar {

	public void populateToolbar(IToolBarManager toolBarManager);

	public void selectionOccured(BindingRipperResult result);

	public static class Utils {
		public static IActionBar actionBar(IExplorer explorer, CardConfig cardConfig, IFunction1<BindingRipperResult, BindingRipperResult> reRipFn, boolean admin) {
			return new ActionBar(explorer, cardConfig, reRipFn, admin);
		}
	}

}

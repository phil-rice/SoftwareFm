package org.softwareFm.eclipse.actions;

import org.eclipse.jface.action.IToolBarManager;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.eclipse.actions.internal.ActionBar;
import org.softwareFm.eclipse.jdtBinding.BindingRipperResult;
import org.softwareFm.swt.configuration.CardConfig;

public interface IActionBar {

	public void populateToolbar(IToolBarManager toolBarManager);

	public void selectionOccured(BindingRipperResult result);

	public static class Utils {
		public static IActionBar actionBar(IExplorer explorer, CardConfig cardConfig, IFunction1<BindingRipperResult, BindingRipperResult> reRipFn, boolean admin) {
			return new ActionBar(explorer, cardConfig, reRipFn, admin);
		}
	}

}

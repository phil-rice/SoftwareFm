package org.softwarefm.core.actions;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.actions.internal.ActionBarComposite;


public interface IActionBar {

	void add(ISfmAction sfmAction);
	
	public static class Utils {
		public static IActionBar actionBarComposite(Composite parent){
			return new ActionBarComposite(parent);
			
		}
	}
}

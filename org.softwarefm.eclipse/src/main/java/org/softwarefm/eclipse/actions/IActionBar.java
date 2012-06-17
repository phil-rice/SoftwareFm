package org.softwarefm.eclipse.actions;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.actions.internal.ActionBarComposite;


public interface IActionBar {

	void add(ISfmAction sfmAction);
	
	public static class Utils {
		public static IActionBar actionBarComposite(Composite parent){
			return new ActionBarComposite(parent);
			
		}
	}
}

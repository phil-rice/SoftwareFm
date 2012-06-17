package org.softwarefm.eclipse.actions;

import org.softwarefm.eclipse.actions.internal.SfmActionBarConfigurator;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.utilities.resources.IResourceGetter;

public interface IActionBarConfigurator {

	public void configure(IActionBar actionBar);
	
	public static class Utils{
		public static IActionBarConfigurator sfmConfigurator(IResourceGetter resourceGetter, SfmActionState sfmActionState, ISelectedBindingManager<?>selectedBindingManager){
			return new SfmActionBarConfigurator(resourceGetter, sfmActionState, selectedBindingManager);
		}
	}
}

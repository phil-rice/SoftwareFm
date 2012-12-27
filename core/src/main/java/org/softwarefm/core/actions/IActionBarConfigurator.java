package org.softwarefm.core.actions;

import org.softwarefm.core.actions.internal.SfmActionBarConfigurator;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.utilities.resources.IResourceGetter;

public interface IActionBarConfigurator {

	public void configure(IActionBar actionBar);
	
	public static class Utils{
		public static IActionBarConfigurator sfmConfigurator(IResourceGetter resourceGetter, SfmActionState sfmActionState, ISelectedBindingManager<?>selectedBindingManager){
			return new SfmActionBarConfigurator(resourceGetter, sfmActionState, selectedBindingManager);
		}
	}
}

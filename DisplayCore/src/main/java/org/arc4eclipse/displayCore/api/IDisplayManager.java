package org.arc4eclipse.displayCore.api;


import org.arc4eclipse.displayCore.api.impl.DisplayManager;

public interface IDisplayManager {

	void registerDisplayer(IDisplayer displayer);

	void populate(IDisplayContainer container, BindingContext bindingContext);

	void addModifier(IModifiesToBeDisplayed modifier);

	void removeModifier(IModifiesToBeDisplayed modifier);

	public static class Utils {
		public static IDisplayManager displayManager() {
			return new DisplayManager(ITitleLookup.Utils.titleLookup());
		}
	}

}

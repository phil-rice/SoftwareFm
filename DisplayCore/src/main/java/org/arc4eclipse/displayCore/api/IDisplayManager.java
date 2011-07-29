package org.arc4eclipse.displayCore.api;

import java.util.Map;

import org.arc4eclipse.displayCore.api.impl.DisplayManager;

public interface IDisplayManager {

	void registerDisplayer(IDisplayer displayer);

	void populate(IDisplayContainer container, BindingContext bindingContext, String url, Map<String, Object> data, Map<String, Object> context);

	void addModifier(IModifiesToBeDisplayed modifier);

	void removeModifier(IModifiesToBeDisplayed modifier);

	public static class Utils {
		public static IDisplayManager displayManager() {
			return new DisplayManager(ITitleLookup.Utils.titleLookup());
		}
	}

}

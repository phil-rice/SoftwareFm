package org.arc4eclipse.displayCore.api;

import java.util.Map;

import org.arc4eclipse.displayCore.api.impl.DisplayManager;

public interface IDisplayManager {

	void registerDisplayer(IDisplayer displayer);

	void populate(IDisplayContainer container, Map<String, Object> jsonObject);

	public static class Utils {
		public static IDisplayManager displayManager() {
			return new DisplayManager(ITitleLookup.Utils.titleLookup());
		}
	}

}

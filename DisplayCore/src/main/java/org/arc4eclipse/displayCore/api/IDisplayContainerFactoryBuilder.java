package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.displayCore.api.impl.DisplayContainerFactory;
import org.eclipse.swt.widgets.Control;

public interface IDisplayContainerFactoryBuilder {
	<L extends Control, C extends Control> void registerDisplayer(IDisplayer<L, C> displayer);

	void registerForEntity(String entity, String key, String title, String help);

	IDisplayContainerFactory build();

	public static class Utils {
		public static IDisplayContainerFactoryBuilder displayManager() {
			return new DisplayContainerFactory();
		}
	}

}

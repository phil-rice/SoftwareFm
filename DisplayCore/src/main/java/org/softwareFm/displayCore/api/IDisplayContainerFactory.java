package org.softwareFm.displayCore.api;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;

public interface IDisplayContainerFactory {

	IDisplayContainer create(DisplayerContext displayerContext, Composite parent);

	/** Resources here are "key" the name of the field, "display" the displayer to use, "editor" the editor to use, "validator" the validator to use, and any cargo things that the displayer needs */
	void register(Map<String, String> map);

}

package org.arc4eclipse.displayCore.api;

import org.eclipse.swt.widgets.Composite;

/** This represents the data about an entity */
public interface IDisplayContainerForTests extends IDisplayContainer {

	Composite compButtons();

	public <L> L getLargeControlFor(String key);

	public <C> C getSmallControlFor(String key);
}

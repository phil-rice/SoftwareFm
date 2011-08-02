package org.arc4eclipse.displayCore.api;

import org.eclipse.swt.widgets.Composite;

public interface IDisplayContainerFactory {

	IDisplayContainer create(DisplayerContext displayerContext, Composite parent, String entity);

}

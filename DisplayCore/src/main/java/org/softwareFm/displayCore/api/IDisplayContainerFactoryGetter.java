package org.softwareFm.displayCore.api;

import org.eclipse.swt.widgets.Display;

public interface IDisplayContainerFactoryGetter {
	IDisplayContainerFactory getDisplayContainerFactory(final Display display, final String viewName);
}

package org.softwareFm.explorer.eclipse;

import org.eclipse.core.runtime.IConfigurationElement;

public interface IPlugInCreationCallback<T> {

	void process(T t, IConfigurationElement element) throws Exception;

	void onException(Throwable throwable, IConfigurationElement element);

}

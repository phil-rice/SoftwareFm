package org.softwareFm.core.plugin;

import org.eclipse.core.runtime.IConfigurationElement;

public abstract class PlugInSysErrAdapter<T> implements IPlugInCreationCallback<T> {

	@Override
	public void onException(Throwable throwable, IConfigurationElement element) {
		System.err.println("Element Contributor: " + element.getContributor());
		throwable.printStackTrace();
	}

}

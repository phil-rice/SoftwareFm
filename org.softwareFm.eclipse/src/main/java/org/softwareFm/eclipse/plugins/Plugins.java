package org.softwareFm.eclipse.plugins;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;

public class Plugins {

	@SuppressWarnings("unchecked")
	public static <T> Class<T> classFrom(IConfigurationElement element) {
		try {
			return (Class<T>) element.createExecutableExtension("class").getClass();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <T> void useConfigElements(String id, ICallback<IConfigurationElement> callback, ICallback<Throwable> exceptions) {
		ICallback<IConfigurationElement> safeCallback = ICallback.Utils.safeCallback(exceptions, callback);
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(id);
		for (IConfigurationElement e : config)
			try {
				safeCallback.process(e);
			} catch (Exception e1) {
				try {
					exceptions.process(e1);
				} catch (Exception e2) {
					throw WrappedException.wrap(e2);
				}
			}
	}

	public static <T> T configureMainWithCallbacks(final T main, String id, final String propertyName, ICallback<Throwable> exceptions) {
		useConfigElements(id, new ICallback<IConfigurationElement>() {
			@Override
			public void process(IConfigurationElement t) throws Exception {
				ICallback<T> callback = (ICallback<T>) t.createExecutableExtension(propertyName);
				callback.process(main);

			}
		}, exceptions);
		return main;
	}

	@SuppressWarnings("unchecked")
	public static <T> void useClasses(String id, IPlugInCreationCallback<T> callback) {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(id);
		for (IConfigurationElement element : config)
			try {
				T t = (T) element.createExecutableExtension("class");
				callback.process(t, element);
			} catch (Exception e1) {
				try {
					callback.onException(e1, element);
				} catch (Exception e2) {
					throw WrappedException.wrap(e2);
				}
			}
	}
}

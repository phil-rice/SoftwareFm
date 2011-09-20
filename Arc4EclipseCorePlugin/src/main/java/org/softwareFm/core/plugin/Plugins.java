package org.softwareFm.core.plugin;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.indent.Indent;

public class Plugins {

	public static Logger logger = Logger.getLogger(Plugins.class);
	
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

	@SuppressWarnings("unchecked")
	public static <T> void useClasses(String id, IPlugInCreationCallback<T> callback) {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(id);
		logger.debug("Using: " + id + " with " + callback);
		for (IConfigurationElement element : config)
			try {
				logger.debug(" element: " + element);
				T t = (T) element.createExecutableExtension("class");
				callback.process(t, element);
				logger.debug("   ....processed: " + t);
			} catch (Exception e1) {
				try {
					logger.error("Processing " + id + " with element " + element, e1);
					callback.onException(e1, element);
				} catch (Exception e2) {
					logger.error("Callback caused another error processing " + id + " with element " + element, e2);
					throw WrappedException.wrap(e2);
				}
			}
	}
}

package arc4eclipse.core.plugin;

import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

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

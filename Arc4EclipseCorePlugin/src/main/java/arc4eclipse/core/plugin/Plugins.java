package arc4eclipse.core.plugin;

import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class Plugins {

	@SuppressWarnings("unchecked")
	public static <T> void useClasses(String id, ICallback<T> callback, ICallback<Throwable> exceptions) {
		ICallback<T> safeCallback = ICallback.Utils.safeCallback(exceptions, callback);
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(id);
		for (IConfigurationElement e : config)
			try {
				safeCallback.process((T) e.createExecutableExtension("class"));
			} catch (Exception e1) {
				try {
					exceptions.process(e1);
				} catch (Exception e2) {
					throw WrappedException.wrap(e2);
				}
			}
	}
}

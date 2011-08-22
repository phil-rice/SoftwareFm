package arc4eclipse.core.plugin;

import org.eclipse.core.runtime.IConfigurationElement;

public interface IPlugInCreationCallback<T> {

	void process(T t, IConfigurationElement element);

	void onException(Throwable throwable);

}

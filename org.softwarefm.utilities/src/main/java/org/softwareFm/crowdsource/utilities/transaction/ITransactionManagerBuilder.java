package org.softwareFm.crowdsource.utilities.transaction;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback3;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;

public interface ITransactionManagerBuilder extends ITransactionManager {

	/** this is used for the Swt thread, to ensure that SwtCallbacks are executed in the Swt thread. It can obviously be used with other marker interfaces */
	<C extends ICallback<?>> ITransactionManagerBuilder registerCallbackExecutor(Class<C> markerClass, ICallback3<IServiceExecutor, ICallback<?>, Object> executor);

}

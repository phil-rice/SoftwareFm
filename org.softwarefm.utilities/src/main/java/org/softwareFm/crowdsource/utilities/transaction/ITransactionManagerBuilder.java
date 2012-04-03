package org.softwareFm.crowdsource.utilities.transaction;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;

public interface ITransactionManagerBuilder extends ITransactionManager {

	/** this is used for the Swt thread, to ensure that SwtCallbacks are executed in the Swt thread. It can obviously be used with other marker interfaces */
	<Intermediate, Result> ITransactionManagerBuilder registerCallbackExecutor(Class<?> markerClass, IFunction3<IServiceExecutor, IFunction1<Intermediate, Result>, Intermediate, Result> executor);

}

package org.softwareFm.crowdsource.utilities.transaction;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;

public interface ITransactionExecutor {

	<T> void call(IServiceExecutor executor, ICallback<T> callback, T value) throws Exception;

	<Result, Intermediate> Result call(IServiceExecutor executor, IFunction1<Intermediate, Result> function, Intermediate value) throws Exception;

	public static class Utils {
		public static ITransactionExecutor onSameThread() {
			return new ITransactionExecutor() {
				@Override
				public <Result, Intermediate> Result call(IServiceExecutor executor, IFunction1<Intermediate, Result> function, Intermediate value) throws Exception {
					return function.apply(value);
				}

				@Override
				public <T> void call(IServiceExecutor executor, ICallback<T> callback, T value) throws Exception {
					callback.process(value);
				}
			};
		}
	}

}

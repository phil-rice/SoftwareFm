/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
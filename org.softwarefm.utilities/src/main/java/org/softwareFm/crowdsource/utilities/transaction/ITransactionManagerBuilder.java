/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.transaction;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;

public interface ITransactionManagerBuilder extends ITransactionManager {

	/** this is used for the Swt thread, to ensure that SwtCallbacks are executed in the Swt thread. It can obviously be used with other marker interfaces */
	<Intermediate, Result> ITransactionManagerBuilder registerCallbackExecutor(Class<?> markerClass, IFunction3<IServiceExecutor, IFunction1<Intermediate, Result>, Intermediate, Result> executor);

}
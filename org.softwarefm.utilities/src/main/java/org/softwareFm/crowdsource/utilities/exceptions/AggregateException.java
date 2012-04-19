/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.exceptions;

import java.util.List;

import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.utililties.aggregators.IAggregator;

public class AggregateException extends RuntimeException {

	private final List<Exception> exceptions;

	public AggregateException(List<Exception> exceptions) {
		super(Iterables.aggregate(exceptions, IAggregator.Utils.join(new IFunction1<Exception, String>() {
			@Override
			public String apply(Exception from) throws Exception {
				return from.getClass() + "/" + from.getMessage();
			}
		}, ",")));
		this.exceptions = exceptions;
	}

	public List<Exception> getExceptions() {
		return exceptions;
	}

}
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.transaction;

import java.util.concurrent.CountDownLatch;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public class ConstantFnWithKick<From, To> implements IFunction1<From, To> {
	private final To result;
	private final CountDownLatch latch = new CountDownLatch(1);

	public ConstantFnWithKick(To result) {
		super();
		this.result = result;
	}

	@Override
	public To apply(From from) throws Exception {
		latch.await();
		return result;
	}

	public void kick() {
		latch.countDown();
	}

}
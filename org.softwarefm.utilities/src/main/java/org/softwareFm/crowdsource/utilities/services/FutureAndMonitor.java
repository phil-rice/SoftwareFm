/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.services;

import java.util.concurrent.Future;

import org.softwareFm.crowdsource.utilities.monitor.IMonitor;

public class FutureAndMonitor<T> {

	public final Future<T> future;
	public final IMonitor monitor;

	public FutureAndMonitor(Future<T> future, IMonitor monitor) {
		super();
		this.future = future;
		this.monitor = monitor;
	}

	@Override
	public String toString() {
		return "FutureAndMonitor [future=" + future + ", monitor=" + monitor + "]";
	}
}
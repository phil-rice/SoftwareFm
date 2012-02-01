/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.monitor;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractMonitor implements IMonitor {

	private final AtomicBoolean cancel = new AtomicBoolean();
	private final AtomicBoolean finished = new AtomicBoolean();

	@Override
	public void cancel() {
		cancel.set(true);
	}

	@Override
	public boolean cancelled() {
		return cancel.get();
	}

	@Override
	public void finish() {
		finished.set(true);
	}

	@Override
	public boolean done() {
		return finished.get();
	}

}
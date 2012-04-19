/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.services.internal;

import java.text.MessageFormat;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class NamedThreadFactory implements ThreadFactory {
	private final AtomicInteger count = new AtomicInteger();
	private final String pattern;

	public NamedThreadFactory(String pattern) {
		super();
		this.pattern = pattern;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, MessageFormat.format(pattern, count.getAndIncrement()));
		thread.setDaemon(true);
		return thread;
	}
}
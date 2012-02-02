/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.runnable;

import java.util.concurrent.atomic.AtomicInteger;

public class Runnables {

	public static class CountRunnable implements Runnable {
		private final AtomicInteger count = new AtomicInteger();

		@Override
		public void run() {
			count.incrementAndGet();
			detail();
		}

		protected void detail() {
		}

		public int getCount() {
			return count.get();
		}
	}

	public static Runnable noRunnable = new Runnable() {
		@Override
		public void run() {
		}
	};

	public static CountRunnable count() {
		return new CountRunnable();
	}

	public static Runnable sysout(final String string) {
		return new Runnable() {
			@Override
			public void run() {
				System.out.println(string);
			}
		};
	}

}
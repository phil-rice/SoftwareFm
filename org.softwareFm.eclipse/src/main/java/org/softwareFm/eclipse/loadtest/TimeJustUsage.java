/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.loadtest;

import org.softwareFm.server.IUsage;

public class TimeJustUsage {
	public static void main(String[] args) {
		IUsage usage = IUsage.Utils.defaultUsage();
		long startTime = System.currentTimeMillis();
		usage.start();
		for (int i = 0; i < 1000; i++)
			usage.monitor("test", "value", i);
		System.out.println("took: " + (System.currentTimeMillis() - startTime) / 1000);

	}
}
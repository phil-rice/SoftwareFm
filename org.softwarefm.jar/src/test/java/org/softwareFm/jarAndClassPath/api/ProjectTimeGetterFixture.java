/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.api;

import java.util.Arrays;

import org.softwareFm.jarAndClassPath.api.IProjectTimeGetter;

public class ProjectTimeGetterFixture implements IProjectTimeGetter {

	@Override
	public String thisMonth() {
		throw new IllegalStateException();
	}

	@Override
	public int day() {
		throw new IllegalStateException();
	}

	@Override
	public Iterable<String> lastNMonths(int n) {
		return Arrays.asList("january_12", "febuary_12", "march_12");
	}

}
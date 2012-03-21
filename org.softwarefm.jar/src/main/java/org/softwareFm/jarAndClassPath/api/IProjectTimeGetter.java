/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.api;

import java.util.List;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.jarAndClassPath.internal.ProjectTimeGetter;

public interface IProjectTimeGetter {

	String thisMonth();

	int day();

	Iterable<String> lastNMonths(int n);

	public static class Utils {
		public static IProjectTimeGetter timeGetter() {
			return new ProjectTimeGetter(Callables.calander());
		}

		public static IProjectTimeGetter mockJanFebMar(final int day) {
			return mock(day, "january_12", "febuary_12", "march_12");
			
		}
		public static IProjectTimeGetter mock(final int day, final String... months) {
			return new IProjectTimeGetter() {

				@Override
				public String thisMonth() {
					return months[months.length - 1];
				}

				@Override
				public Iterable<String> lastNMonths(int n) {
					List<String> result = Lists.newList();
					for (int i = months.length - n; i < months.length; i++)
						result.add(months[i]);
					return result;
				}

				@Override
				public int day() {
					return day;
				}
			};
		}
	}

}
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.internal;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.runnable.Callables;

public class ProjectTimeGetterTest extends TestCase {

	public void testDayAndMonth() {
		checkDayAndMonth(2012, 1, 5, "febuary_12");
		checkDayAndMonth(2013, 2, 2, "march_13");
	}

	public void testLastNMonths() {
		checkLastNMonths(2012, 3, 3, "april_12", "march_12", "febuary_12");
		checkLastNMonths(2012, 1, 3, "febuary_12", "january_12", "december_11");
		checkLastNMonths(2012, 0, 3, "january_12", "december_11", "november_11");

		checkLastNMonths(2012, 3, 1, "april_12");
		checkLastNMonths(2012, 1, 1, "febuary_12");
		checkLastNMonths(2012, 0, 1, "january_12");
	}

	private void checkLastNMonths(int year, int month, int n, String... expected) {
		assertEquals(n, expected.length);
		Callable<Calendar> calendar = Callables.calander(year, month, 1, 10, 12);
		ProjectTimeGetter timeGetter = new ProjectTimeGetter(calendar);
		assertEquals(Arrays.asList(expected), timeGetter.lastNMonths(n));
	}

	protected void checkDayAndMonth(int year, int month, int day, String expectedMonth) {
		Callable<Calendar> calendar = Callables.calander(year, month, day, 10, 12);
		ProjectTimeGetter timeGetter = new ProjectTimeGetter(calendar);
		assertEquals(expectedMonth, timeGetter.thisMonth());
		assertEquals(day, timeGetter.day());
	}

}
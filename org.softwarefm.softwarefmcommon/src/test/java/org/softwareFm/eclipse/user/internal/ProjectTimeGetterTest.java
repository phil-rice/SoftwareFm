package org.softwareFm.eclipse.user.internal;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

import org.softwareFm.common.runnable.Callables;

public class ProjectTimeGetterTest extends TestCase {

	public void testDayAndMonth() {
		checkDayAndMonth(2012, 1, 5, "febuary_12");
		checkDayAndMonth(2013, 2, 2, "march_13");
	}

	public void testLastNMonths() {
		checkLastNMonths(2012, 3, 3, "april_12", "march_12", "febuary_12");
		checkLastNMonths(2012, 1, 3, "febuary_12", "january_12", "december_11");
		checkLastNMonths(2012, 0, 3,  "january_12", "december_11", "november_11");

		checkLastNMonths(2012, 3, 1, "april_12");
		checkLastNMonths(2012, 1, 1, "febuary_12");
		checkLastNMonths(2012, 0, 1,  "january_12");
	}

	private void checkLastNMonths(int year, int month, int n, String ...expected) {
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

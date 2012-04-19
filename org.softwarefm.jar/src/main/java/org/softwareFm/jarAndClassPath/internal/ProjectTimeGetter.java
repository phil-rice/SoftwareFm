/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.internal;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.jarAndClassPath.api.IProjectTimeGetter;

public class ProjectTimeGetter implements IProjectTimeGetter {
	private final String[] month2String = new String[] { "january", "febuary", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december" };

	private final Callable<Calendar> calendarGetter;

	public ProjectTimeGetter(Callable<Calendar> calendarGetter) {
		this.calendarGetter = calendarGetter;
	}

	@Override
	public String thisMonth() {
		Calendar calendar = makeCalendar();
		return monthForCalendar(calendar);
	}

	protected String monthForCalendar(Calendar calendar) {
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR) % 100;
		return MessageFormat.format("{0}_{1}", month2String[month], year);
	}

	@Override
	public int day() {
		Calendar calendar = makeCalendar();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	@Override
	public Iterable<String> lastNMonths(int n) {
		Calendar calendar = makeCalendar();
		Calendar copy = (Calendar) calendar.clone();
		List<String> result = Lists.newList();
		for (int i = 0; i < n; i++) {
			result.add(monthForCalendar(copy));
			copy.add(Calendar.MONTH, -1);
		}
		return result;
	}

	protected Calendar makeCalendar() {
		return Callables.call(calendarGetter);
	}

}
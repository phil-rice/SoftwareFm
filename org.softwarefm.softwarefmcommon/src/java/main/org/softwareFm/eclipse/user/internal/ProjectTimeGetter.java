package org.softwareFm.eclipse.user.internal;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.eclipse.user.IProjectTimeGetter;

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
		int year = calendar.get(Calendar.YEAR)%100;
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
		for (int i = 0; i<n; i++){
			result.add(monthForCalendar(copy));
			copy.add(Calendar.MONTH, -1);
		}
		return result;
	}

	protected Calendar makeCalendar() {
		return Callables.call(calendarGetter);
	}

}

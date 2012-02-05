package org.softwareFm.eclipse.user;

import java.util.Calendar;

import org.softwareFm.eclipse.user.internal.ProjectTimeGetter;

public interface IProjectTimeGetter {

	String thisMonth();

	int day();

	Iterable<String> lastNMonths(int n);

	public static class Utils {
		public static IProjectTimeGetter timeGetter() {
			return new ProjectTimeGetter(Calendar.getInstance());
		}
	}

}

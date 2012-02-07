package org.softwareFm.eclipse.user;

import org.softwareFm.common.runnable.Callables;
import org.softwareFm.eclipse.user.internal.ProjectTimeGetter;

public interface IProjectTimeGetter {

	String thisMonth();

	int day();

	Iterable<String> lastNMonths(int n);

	public static class Utils {
		public static IProjectTimeGetter timeGetter() {
			return new ProjectTimeGetter(Callables.calander());
		}
	}

}

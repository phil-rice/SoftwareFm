package org.softwareFm.utilities.exceptions;

import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.strings.Strings;

public class Exceptions {

	public static String stackTraceString(StackTraceElement[] stackTrace, String separator) {
		return Strings.join(Iterables.iterable(stackTrace), separator);
	}

}

package org.arc4eclipse.utilities.exceptions;

import org.arc4eclipse.utilities.collections.Iterables;
import org.arc4eclipse.utilities.strings.Strings;

public class Exceptions {

	public static String stackTraceString(StackTraceElement[] stackTrace, String separator) {
		return Strings.join(Iterables.iterable(stackTrace), separator);
	}

}

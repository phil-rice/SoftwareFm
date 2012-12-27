/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.exceptions;

import org.softwarefm.utilities.collections.Iterables;
import org.softwarefm.utilities.strings.Strings;

public class Exceptions {

	public static String stackTraceString(StackTraceElement[] stackTrace, String separator) {
		return Strings.join(Iterables.iterable(stackTrace), separator);
	}

	public static String classAndMessage(Exception e) {
		String raw = e.getClass().getSimpleName();
		return e.getMessage() == null ? raw : raw + ", " + e.getMessage();
	}

	public static String serialize(Exception e, String separator) {
		return classAndMessage(e)+separator +stackTraceString(e.getStackTrace(), separator);
	}

}
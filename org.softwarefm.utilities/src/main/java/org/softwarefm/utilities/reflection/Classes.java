/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.reflection;

import java.io.File;

import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.functions.IFunction1;

public class Classes {

	public static IFunction1<File, Class<?>> asClass(final IClassAcceptor classAcceptor) {
		return new IFunction1<File, Class<?>>() {
			public Class<?> apply(File from) throws Exception {
				String[] segments = from.getCanonicalPath().split("\\" + File.separator);
				int start = findStart(segments);
				if (start == -1)
					return null;
				StringBuilder builder = new StringBuilder();
				for (int i = start; i < segments.length; i++) {
					if (builder.length() > 0)
						builder.append('.');
					builder.append(Files.noExtension(segments[i]));
				}
				try {
					Class<?> clazz = Class.forName(builder.toString());
					if (classAcceptor.accept(from, clazz))
						return clazz;
				} catch (ClassNotFoundException e) {
				}
				return null;
			}

			private int findStart(String[] segments) {
				for (int i = segments.length - 1; i >= 0; i--) {
					String segment = segments[i];
					if (segment.equals("org"))
						return i;
				}
				return -1;
			}

		};
	}
}
package org.arc4eclipse.utilities.reflection;

import java.io.File;

import org.arc4eclipse.utilities.collections.Files;
import org.arc4eclipse.utilities.functions.IFunction1;

public class Classes {

	public static IFunction1<File, Class<?>> asClass(final IClassAcceptor classAcceptor) {
		return new IFunction1<File, Class<?>>() {
			@Override
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

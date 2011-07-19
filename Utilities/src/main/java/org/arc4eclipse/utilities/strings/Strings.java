package org.arc4eclipse.utilities.strings;

import java.util.List;

import org.arc4eclipse.utilities.aggregators.IAggregator;
import org.arc4eclipse.utilities.functions.IFunction1;

public class Strings {
	public static <T> String join(Iterable<T> from, String separator) {
		StringBuilder builder = new StringBuilder();
		boolean addSeparator = false;
		for (T f : from) {
			if (addSeparator)
				builder.append(separator);
			builder.append(f);
			addSeparator = true;
		}
		return builder.toString();
	}

	public static String join(List<String> from, List<Integer> indicies, String separator) {
		StringBuilder builder = new StringBuilder();
		boolean addSeparator = false;
		for (int i = 0; i < indicies.size(); i++) {
			String f = from.get(indicies.get(i));
			if (addSeparator)
				builder.append(separator);
			builder.append(f);
			addSeparator = true;
		}
		return builder.toString();
	}

	public static String asData(Object value, int length) {
		if (value == null)
			return asData("", length);
		String valueAsString = value instanceof Double ? String.format("%" + length + ".3f", value) : value.toString();
		int deficit = length - valueAsString.length();
		if (deficit <= 0)
			return valueAsString;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < deficit; i++)
			builder.append(' ');
		builder.append(valueAsString);
		return builder.toString();
	}

	public static String oneLine(String string) {
		return string.replaceAll("\n", " ");
	}

	public static IAggregator<String, String> strJoin(final String separator) {
		return Strings.<String> join(separator);
	}

	public static <T> IAggregator<T, String> join(final String separator) {
		return new IAggregator<T, String>() {
			private final StringBuilder builder = new StringBuilder();
			private boolean addSeparator;

			@Override
			public void add(T t) {
				if (addSeparator)
					builder.append(separator);
				addSeparator = true;
				builder.append(t);
			}

			@Override
			public String result() {
				return builder.toString();
			}
		};

	}

	public static IFunction1<String, Integer> length() {
		return new IFunction1<String, Integer>() {
			@Override
			public Integer apply(String from) throws Exception {
				return from.length();
			}
		};
	}

	public static IFunction1<Object, String> toStringFn() {
		return new IFunction1<Object, String>() {
			@Override
			public String apply(Object from) throws Exception {
				return from.toString();
			}
		};
	}

	public static String bracket(String raw, String brackets) {
		assert brackets.length() == 2;
		return brackets.charAt(0) + raw + brackets.charAt(1);
	}

	public static String quote(String raw) {
		return '"' + raw + '"';
	}

}

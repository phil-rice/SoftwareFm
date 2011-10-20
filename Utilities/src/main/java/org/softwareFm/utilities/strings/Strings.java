package org.softwareFm.utilities.strings;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;

import org.softwareFm.utilities.aggregators.IAggregator;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;

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

	public static List<String> splitIgnoreBlanks(String raw, String separator) {
		List<String> result = Lists.newList();
		for (String string : raw.split(separator))
			if (string.length() > 0)
				result.add(string);
		return result;
	}

	public static PreAndPost split(String value, char separator) {
		int index = value.indexOf(separator);
		if (index == -1)
			return new PreAndPost(value, null);
		else
			return new PreAndPost(value.substring(0, index), value.substring(index + 1));

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

	public static String oneLine(Object object) {
		return nullSafeToString(object).replaceAll("\n", " ").replaceAll("\r", " ");
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

	public static String onlyKeep(String raw, String chars) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < raw.length(); i++) {
			char ch = raw.charAt(i);
			if (chars.indexOf(ch) != -1)
				builder.append(ch);
		}
		return builder.toString();
	}

	public static IFunction1<String, Boolean> startsWith(final String prefix) {
		return new IFunction1<String, Boolean>() {
			@Override
			public Boolean apply(String from) throws Exception {
				return from.startsWith(prefix);
			}
		};
	}

	public static String addToRollingLog(List<String> logger, int size, String separator, String value) {
		logger.add(0, value);
		if (logger.size() >= size)
			logger.remove(logger.size() - 1);
		return join(logger, separator);
	}

	public static String nullSafeToString(Object value) {
		if (value == null)
			return "";
		else
			return value.toString();
	}

	public static boolean safeEquals(String string1, String string2) {
		if (string1 == string2)
			return true;
		if (string1 == null || string2 == null)
			return false;
		return string1.equals(string2);
	}

	public static boolean hasValue(Object object) {
		if (object == null)
			return false;
		String toString = object.toString();
		return toString.length() > 0;
	}

	public static String oneLineLowWhiteSpace(String value) {
		return Iterables.aggregate(Arrays.asList(value.split("\n")), new IAggregator<String, String>() {
			private final StringBuilder builder = new StringBuilder();

			@Override
			public void add(String t) {
				String trimmed = t.trim();
				if (trimmed.length() > 0) {
					if (builder.length() > 0)
						builder.append(" ");
					builder.append(trimmed);
				}
			}

			@Override
			public String result() {
				return builder.toString();
			}
		});
	}

	public static String sqlEscape(String raw) {
		return raw.replaceAll("'", "''").replaceAll("\\\\", "\\\\");
	}

	public static String forUrl(String raw) {
		String cleanUrl = Strings.onlyKeep(raw.toLowerCase(), "abcdefghijklmnopqrstuvwxyz0123456789._-");
		return cleanUrl;
	}

	public static IFunction1<String, String> lastSegmentFn(final String separator) {
		return new IFunction1<String, String>() {

			@Override
			public String apply(String from) throws Exception {
				int index = from.lastIndexOf(separator);
				if (index == -1)
					return from;
				else
					return from.substring(index + 1);
			}
		};
	}

	public static IFunction1<String, String> camelCaseToPrettyFn() {
		return new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return camelCaseToPretty(from);
			}
		};

	}

	public static String camelCaseToPretty(String raw) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < raw.length(); i++) {
			char ch = raw.charAt(i);
			if (result.length() == 0) {
				if (Character.isLetter(ch))
					result.append(Character.toUpperCase(ch));
				else if (ch != ' ')
					result.append(ch);
			} else {
				if (ch == ' ')
					if (result.length() == 0 || result.charAt(result.length() - 1) == ' ')
						;// ignore
					else
						result.append(' ');
				else if (Character.isUpperCase(ch)) {
					if (result.length() > 0 && result.charAt(result.length() - 1) != ' ')
						result.append(' ');
					result.append(ch);
				} else
					result.append(ch);
			}
		}
		return result.toString();
	}

	public static void setClipboard(String str) {
		StringSelection ss = new StringSelection(str);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}
}

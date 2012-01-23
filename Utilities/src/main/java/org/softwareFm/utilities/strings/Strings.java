/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.strings;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.softwareFm.utilities.aggregators.IAggregator;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.functions.IFunction1;

public class Strings {

	private final static Pattern urlFriendlyPattern = Pattern.compile("^[\\w\\.-]+$");
	private static String digits = "0123456789abcdef";

	public static boolean isEmail(String email) {
		Matcher matcher = Pattern.compile("[\\w-]+@([\\w-]+\\.)+[\\w-]+").matcher(email);
		boolean emailOk = email.length() > 0 && matcher.find();
		return emailOk;
	}

	public static String toHex(byte[] data, int length) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i != length; i++) {
			int v = data[i] & 0xff;

			buf.append(digits.charAt(v >> 4));
			buf.append(digits.charAt(v & 0xf));
		}

		return buf.toString();
	}

	/**
	 * Return the passed in byte array as a hex string.
	 * 
	 * @param data
	 *            the bytes to be converted.
	 * @return a hex representation of data.
	 */
	public static String toHex(byte[] data) {
		return toHex(data, data.length);
	}

	public static byte[] fromHex(String hexCoded) {
		if (hexCoded.length() % 2 != 0)
			throw new IllegalArgumentException(hexCoded);
		byte[] result = new byte[hexCoded.length() / 2];
		int index = 0;
		int last = 0;
		for (int i = 0; i < hexCoded.length(); i++) {
			byte thisChar = (byte) hexCoded.charAt(i);
			byte byte0 = (byte) '0';
			byte charOffset = 39;
			int nibble = thisChar - byte0;
			if (nibble > 9)
				nibble = nibble - charOffset;
			if (nibble < 0 || nibble > 15)
				throw new IllegalArgumentException();
			switch (i % 2) {
			case 0:
				last = nibble;
				break;
			case 1:
				result[index++] = (byte) (last * 16 + nibble);
				break;
			}

		}
		return result;
	}

	public static synchronized boolean isUrlFriendly(String raw) {
		return urlFriendlyPattern.matcher(raw).matches();
	}

	public static String htmlEscape(String raw) {
		return raw.replace("<", "&lt;").replace(">", "&gt;");

	}

	public static String removeBrackets(String raw, char open, char close) {
		if (raw == null)
			return null;
		StringBuilder builder = new StringBuilder();
		int depth = 0;
		for (int i = 0; i < raw.length(); i++) {
			char ch = raw.charAt(i);
			if (ch == open)
				depth++;
			else if (ch == close)
				depth--;
			else if (depth == 0)
				builder.append(ch);
		}
		return builder.toString();

	}

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

	public static String replaceColonWithUnderscore(String raw) {
		if (raw == null)
			throw new NullPointerException();
		else
			return raw.replace(":", "_");
	}

	public static List<String> splitIgnoreBlanks(String raw, String separator) {
		List<String> result = Lists.newList();
		if (raw != null)
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

	public static String versionPartOf(File raw, String defaultIfCannotFind) {
		if (raw != null) {
			String version = lastSegment(raw.toString(), "-");
			if (isVersion(version)) {
				int index = version.lastIndexOf('.');
				if (index != -1) {
					String result = version.substring(0, index);
					return result;
				}
			}
		}
		return defaultIfCannotFind;
	}

	public static String withoutVersion(File raw, String defaultIfCannotFind) {
		if (raw != null) {
			String file = raw.getName();
			String version = lastSegment(file, "-");
			if (isVersion(version)) {
				int index = file.lastIndexOf("-");
				if (index != -1) {
					String result = file.substring(0, index);
					return result;
				}
			}
		}
		return defaultIfCannotFind;

	}

	public static boolean isVersion(String version) {
		Set<String> releaseStrings = Sets.makeSet("RELEASE", "Final");
		List<String> list = splitIgnoreBlanks(Files.noExtension(version), "\\.");
		if (list.size() > 0)
			for (int i = 0; i < list.size(); i++) {
				String item = list.get(i);
				if (!releaseStrings.contains(item))
					for (int j = 0; j < item.length(); j++)
						if (!Character.isDigit(item.charAt(j)))
							return false;
			}
		return true;
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

	public static IFunction1<String, String> forUrlFn() {
		return new IFunction1<String, String>() {

			@Override
			public String apply(String from) throws Exception {
				return forUrl(from);
			}
		};
	}

	public static String forUrl(String raw) {
		String cleanUrl = Strings.onlyKeep(raw, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789._-");
		return cleanUrl;
	}

	public static IFunction1<String, String> lastSegmentFn(final String separator) {
		return new IFunction1<String, String>() {

			@Override
			public String apply(String from) throws Exception {
				return lastSegment(from, separator);
			}

		};
	}

	public static String lastSegment(String from, final String separator) {
		if (from == null)
			return from;
		int index = from.lastIndexOf(separator);
		if (index == -1)
			return from;
		else
			return from.substring(index + 1);
	}

	public static String allButLastSegment(String from, final String separator) {
		if (from == null)
			return from;
		int index = from.lastIndexOf(separator);
		if (index == -1)
			return from;
		else
			return from.substring(0, index);

	}

	public static IFunction1<String, String> upperCaseFirstCharacterFn() {
		return new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return upperCaseFirstCharacter(from);
			}
		};
	}

	public static String upperCaseFirstCharacter(String from) {
		if (from == null || from.length() < 1)
			return from;
		return Character.toUpperCase(from.charAt(0)) + from.substring(1);
	}

	public static String lowerCaseFirstCharacter(String from) {
		if (from == null || from.length() < 1)
			return from;
		return Character.toLowerCase(from.charAt(0)) + from.substring(1);
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
						doNothing();// ignore
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

	private static void doNothing() {
	}

	public static void setClipboard(String str) {
		StringSelection ss = new StringSelection(str);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}

	public static Comparator<String> compareVersionNumbers() {
		return new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return compareVersionNumbers(o1, o2);
			}
		};

	}

	public static int compareVersionNumbers(String o1, String o2) {
		Pattern numRegEx = Pattern.compile("\\d*");
		String[] left = o1.split("[\\.-]");
		String[] right = o2.split("[\\.-]");
		for (int i = 0; i < Math.max(left.length, right.length); i++) {
			if (i >= left.length)
				return -1;
			if (i >= right.length)
				return 1;
			String l = left[i];
			String r = right[i];
			if (numRegEx.matcher(l).matches() && numRegEx.matcher(r).matches()) {
				int compare = Integer.parseInt(l) - Integer.parseInt(r);
				if (compare != 0)
					return compare;
			} else {
				int compare = l.compareTo(r);
				if (compare != 0)
					return compare;
			}
		}
		return 0;
	}

	public static String removeNewLines(String raw) {
		return raw.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\f", "");
	}

	public static String firstSegment(String raw, String separator) {
		if (raw == null)
			return null;
		int index = raw.indexOf(separator);
		if (index == -1)
			return raw;
		else
			return raw.substring(0, index);

	}

	public static String segment(String raw, String separator, int i) {
		if (raw == null)
			return raw;
		List<String> split = Strings.splitIgnoreBlanks(raw, separator);
		if (i < split.size())
			return split.get(i);
		else
			return "";
	}

	public static IFunction1<String, String> segmentFn(final String separator, final int i) {
		return new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return segment(from, separator, i);
			}
		};
	}

	public static String oneStartsWith(List<String> roots, String value) {
		for (String root : roots)
			if (value.startsWith(root))
				return root;
		return null;
	}

}
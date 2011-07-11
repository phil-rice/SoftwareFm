package org.arc4eclipse.utilities.strings;

import org.arc4eclipse.utilities.annotations.Slow;

/** This is a very simple string that uses UTF-8 character encoding. It is small and poolable */
public interface ISimpleString {

	int length();

	byte byteAt(int offset);

	@Slow("This will make a new object")
	String asString();

	static class Utils {

		public static boolean equivalent(ISimpleString simpleString, String string) {
			if (simpleString == null || string == null)
				return simpleString == null && string == null;
			int length = simpleString.length();
			if (length != string.length())
				return false;
			for (int i = 0; i < length; i++)
				if (simpleString.byteAt(i) != string.charAt(i))
					return false;
			return true;
		}

		public static final ISimpleString empty = new ISimpleString() {
			public int length() {
				return 0;
			}

			public byte byteAt(int offset) {
				throw new IndexOutOfBoundsException(Integer.toString(offset));
			}

			public String asString() {
				return "";
			}
		};

		public static boolean equivalent(String string, ISimpleString simpleString) {
			if (string == null || simpleString == null)
				return string == null && simpleString == null;
			if (string.length() != simpleString.length())
				return false;
			for (int i = 0; i < string.length(); i++)
				if ((byte) string.charAt(i) != simpleString.byteAt(i))
					return false;
			return true;
		}

	}

}

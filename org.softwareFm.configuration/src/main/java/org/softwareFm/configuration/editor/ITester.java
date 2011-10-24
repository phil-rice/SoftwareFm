package org.softwareFm.configuration.editor;

import org.softwareFm.utilities.exceptions.WrappedException;

public interface ITester {

	String test() throws Exception;

	void processResults(String result);

	public static class Utils {
		public static String test(ITester tester) {
			try {
				String result = tester.test();
				tester.processResults(result);
				return result;
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		public static ITester mock() {
			return new ITester() {

				@Override
				public String test() {
					return null;
				}

				@Override
				public void processResults(String result) {
				}
			};
		}
	}

}

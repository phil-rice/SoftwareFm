package org.softwareFm.jdtBinding.api;

import java.text.MessageFormat;

public interface IJavadocSourceMutatorCallback {

	void process(String requested, String actual);

	public static class Utils {
		public static IJavadocSourceMutatorCallback sysout(final String pattern) {
			return new IJavadocSourceMutatorCallback() {
				@Override
				public void process(String requested, String actual) {
					System.out.println(MessageFormat.format(pattern, requested, actual));
				}
			};
		}

		public static IJavadocSourceMutatorCallback sysout() {
			return sysout("Mutated {0},{1}");
		}

		public static IJavadocSourceMutatorCallback blank() {
			return new IJavadocSourceMutatorCallback() {
				@Override
				public void process(String requested, String actual) {
				}
			};
		}
	}
}

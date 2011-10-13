package org.softwareFm.jdtBinding.api;

import java.text.MessageFormat;

public interface IJavadocSourceMutator {

	public void setNewValue(String newValue, IJavadocSourceMutatorCallback whenComplete) throws Exception;

	public static class Utils {
		public static IJavadocSourceMutator sysout(final String pattern) {
			return new IJavadocSourceMutator() {
				@Override
				public void setNewValue(String newValue, IJavadocSourceMutatorCallback whenComplete) {
					System.out.println(MessageFormat.format(pattern, newValue));
					whenComplete.process(newValue, newValue);
				}
			};
		}

		public static IJavadocSourceMutator mock() {
			return new IJavadocSourceMutator() {
				@Override
				public void setNewValue(String newValue, IJavadocSourceMutatorCallback whenComplete) {
					whenComplete.process(newValue, newValue);
				}
			};
		}
	}

}

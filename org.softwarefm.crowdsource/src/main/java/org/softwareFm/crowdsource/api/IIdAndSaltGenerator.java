package org.softwareFm.crowdsource.api;

import java.text.MessageFormat;
import java.util.UUID;

public interface IIdAndSaltGenerator {

	String makeNewUserId();

	String makeNewGroupId();

	String makeSalt();
	
	String makeMagicString();

	public static class Utils {
		public static IIdAndSaltGenerator uuidGenerators() {
			return new IIdAndSaltGenerator() {
				@Override
				public String makeNewUserId() {
					return UUID.randomUUID().toString();
				}

				@Override
				public String makeSalt() {
					return UUID.randomUUID().toString();
				}

				@Override
				public String makeNewGroupId() {
					return UUID.randomUUID().toString();
				}

				@Override
				public String makeMagicString() {
					return UUID.randomUUID().toString();
				}
			};
		}

		public static IIdAndSaltGenerator mockGenerators(final String userPattern, final String groupPattern, final String saltPattern, final String magicStringPattern) {
			return new IIdAndSaltGenerator() {
				int userIndex;
				int groupIndex;
				int saltIndex;
				int magicStringIndex;

				@Override
				public String makeNewUserId() {
					return MessageFormat.format(userPattern, userIndex++);
				}

				@Override
				public String makeSalt() {
					return MessageFormat.format(groupPattern, saltIndex++);
				}

				@Override
				public String makeNewGroupId() {
					return MessageFormat.format(groupPattern, groupIndex++);
				}
				@Override
				public String makeMagicString() {
					return MessageFormat.format(magicStringPattern, magicStringIndex++);
				}
			};
		}
	}
}

package org.softwareFm.crowdsource.api.newGit;

import org.softwareFm.crowdsource.api.newGit.internal.EncryptedSingleSource;
import org.softwareFm.crowdsource.api.newGit.internal.RawSingleSource;

public interface ISingleSource {

	/**
	 * <ul>
	 * <li>For global single sources this is equal to rl() and file()
	 * <li>For my single sources this is your user id directory with the rl() and file()
	 * <li>For my group sources this is your group id directory with the rl() and file()
	 */
	String fullRl();

	String decypt(String raw);

	String encrypt(String string);

	public static class Utils {
		public static ISingleSource fromRl(String rl, String crypto) {
			return crypto == null ? raw(rl) : encrypted(rl, crypto);
		}

		public static ISingleSource encrypted(String rl, String crypto) {
			return new EncryptedSingleSource(rl, crypto);
		}

		public static ISingleSource raw(String rl) {
			return new RawSingleSource(rl);
		}
	}

}

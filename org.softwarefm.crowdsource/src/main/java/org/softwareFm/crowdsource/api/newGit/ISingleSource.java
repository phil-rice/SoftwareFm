package org.softwareFm.crowdsource.api.newGit;


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

}

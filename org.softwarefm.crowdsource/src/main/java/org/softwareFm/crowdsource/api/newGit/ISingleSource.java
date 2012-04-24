package org.softwareFm.crowdsource.api.newGit;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public interface ISingleSource {

	/**
	 * <ul>
	 * <li>For global single sources this is equal to rl() and file()
	 * <li>For my single sources this is your user id directory with the rl() and file()
	 * <li>For my group sources this is your group id directory with the rl() and file()
	 */
	String fullRl();

	IFunction1<String, String> decyptLine();

	String encrypt(String string);

}

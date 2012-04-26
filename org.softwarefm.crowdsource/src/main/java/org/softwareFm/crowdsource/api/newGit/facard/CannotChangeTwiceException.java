package org.softwareFm.crowdsource.api.newGit.facard;

import java.util.Map;

import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.constants.GitMessages;

public class CannotChangeTwiceException extends RepoException {

	public CannotChangeTwiceException(String pattern, ISingleSource source, int index, Map<String,Object>newMap, Map<String,Object>existingChange) {
		super(GitMessages.cannotMakeChangeToSameItemTwice, source, index, newMap, existingChange);
	}

}

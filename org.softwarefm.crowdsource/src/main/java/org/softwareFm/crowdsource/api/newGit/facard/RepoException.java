package org.softwareFm.crowdsource.api.newGit.facard;

import java.text.MessageFormat;

abstract public class RepoException extends RuntimeException{

	RepoException(String pattern, Object ...args){
		super(MessageFormat.format(pattern, args));
	}
}

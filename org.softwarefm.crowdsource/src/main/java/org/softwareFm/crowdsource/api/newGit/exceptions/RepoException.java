package org.softwareFm.crowdsource.api.newGit.exceptions;

import java.text.MessageFormat;

abstract public class RepoException extends RuntimeException{

	RepoException(String pattern, Object ...args){
		super(MessageFormat.format(pattern, args));
	}
}

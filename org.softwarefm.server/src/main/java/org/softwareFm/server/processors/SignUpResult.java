package org.softwareFm.server.processors;

public class SignUpResult {
	/** null if no error */
	public String errorMessage;
	/** null if error */
	public String crypto;
	public SignUpResult(String errorMessage, String crypto) {
		this.errorMessage = errorMessage;
		this.crypto = crypto;
	}
	
}

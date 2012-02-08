package org.softwareFm.server.processors;

public interface ISignUpChecker {

	SignUpResult signUp(String email, String moniker, String salt, String passwordHash, String softwareFmId);

}

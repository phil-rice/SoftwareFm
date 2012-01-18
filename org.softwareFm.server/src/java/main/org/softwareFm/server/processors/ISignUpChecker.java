package org.softwareFm.server.processors;

public interface ISignUpChecker {

	SignUpResult signUp(String email, String salt, String passwordHash);

}

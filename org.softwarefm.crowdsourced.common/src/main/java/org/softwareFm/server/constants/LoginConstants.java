package org.softwareFm.server.constants;

import org.softwareFm.utilities.url.IUrlGenerator;

public class LoginConstants {

	public static final String emailKey = "email";
	public static final String passwordHashKey = "passwordHash";
	public static final String newPasswordHashKey = "newPasswordHash";
	public static final String sessionSaltKey = "sessionSalt";
	public static final String emailSaltKey = "emailSalt";
	public static final String softwareFmIdKey = "softwareFmId";
	public static final String cryptoKey = "crypto";
	public static final String makeSaltPrefix = "command/makeLoginSalt";
	public static final String loginCommandPrefix = "command/login";
	public static final String forgottonPasswordPrefix = "command/forgottonPassword";
	public static final String passwordResetLinkPrefix = "command/resetPassword";
	public static final String signupPrefix = "command/signup";
	public static final String changePasswordPrefix = "command/changePassword";
	
	public static IUrlGenerator userGenerator() {
		return IUrlGenerator.Utils.generator("softwareFm/users/{0}/{1}/{2}", softwareFmIdKey);
	}
}

package org.softwareFm.server.constants;

public class LoginMessages {

	public static final String passwordChanged = "Password changed";
	public static final String wrongPassword = "Password didn't match email";
	public static final String emailNotRecognised = "Email not recognised";
	public static final String emailAddressNotFound = "Email address {0} not known";
	public static final String passwordResetSubject = "Software FM Password Reset";
	public static final String forgottonPasswordMessage = "You've lost your SoftwareFM password? Say it isn't so!\n\nVisit this site www.softwarefm.org/" + LoginConstants.passwordResetLinkPrefix + "/{1} to reset it.";
	public static final String duplicateEmailAndPassword = "Duplicate email and password {0} and {1}";
	public static final String existingSoftwareFmId = "Existing software fm id {0}";
	public static final String existingEmailAddress = "Existing email address {0}";
	public static final String invalidSaltMessage = "invalid salt message";
	public static final String emailPasswordMismatch = "Email / Password didn't match";
	public static final String passwordResetHtml = "<html><body>Your new password is: {0}</html>";
	public static final String failedToResetPasswordHtml = "<html><body>Failed to reset. Please request a new link</body></html>";
	public static final String cannotDelete = "Cannot delete {0}";
	public static final String cannotClearDirectory = "Cannot clear directory: {0}";

}

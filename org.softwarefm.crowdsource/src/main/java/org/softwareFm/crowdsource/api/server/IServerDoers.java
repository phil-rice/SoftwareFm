package org.softwareFm.crowdsource.api.server;

public interface IServerDoers {

	IEmailSaltRequester getEmailSaltRequester();

	ISignUpChecker getSignUpChecker();

	ITakeOnProcessor getTakeOnProcessor();

	ISaltProcessor getSaltProcessor();

	IUsage getUsage();

	IMailer getMailer();

	ILoginChecker getLoginChecker();

	IForgottonPasswordMailer getForgottonPasswordMailer();

	IPasswordResetter getPasswordResetter();
}

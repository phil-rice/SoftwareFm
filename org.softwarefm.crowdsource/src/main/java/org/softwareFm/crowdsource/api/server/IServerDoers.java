package org.softwareFm.crowdsource.api.server;

import org.softwareFm.crowdsource.navigation.IRepoNavigation;

public interface IServerDoers {

	IEmailSaltRequester getEmailSaltRequester();

	ISignUpChecker getSignUpChecker();

	ITakeOnProcessor getTakeOnProcessor();

	ISaltProcessor getSaltProcessor();

	IRepoNavigation getRepoNavigation();

	IUsage getUsage();

	IMailer getMailer();

	ILoginChecker getLoginChecker();

	IForgottonPasswordMailer getForgottonPasswordMailer();

	IPasswordResetter getPasswordResetter();
}

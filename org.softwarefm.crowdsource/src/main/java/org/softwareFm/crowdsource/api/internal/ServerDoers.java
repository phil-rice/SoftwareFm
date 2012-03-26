package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.server.IEmailSaltRequester;
import org.softwareFm.crowdsource.api.server.IForgottonPasswordMailer;
import org.softwareFm.crowdsource.api.server.ILoginChecker;
import org.softwareFm.crowdsource.api.server.IMailer;
import org.softwareFm.crowdsource.api.server.IPasswordResetter;
import org.softwareFm.crowdsource.api.server.ISaltProcessor;
import org.softwareFm.crowdsource.api.server.IServerDoers;
import org.softwareFm.crowdsource.api.server.ISignUpChecker;
import org.softwareFm.crowdsource.api.server.ITakeOnProcessor;
import org.softwareFm.crowdsource.api.server.IUsage;
import org.softwareFm.crowdsource.server.doers.internal.EmailSaltRequester;
import org.softwareFm.crowdsource.server.doers.internal.ForgottonPasswordMailer;
import org.softwareFm.crowdsource.server.doers.internal.LoginChecker;
import org.softwareFm.crowdsource.server.doers.internal.PasswordResetter;
import org.softwareFm.crowdsource.server.doers.internal.SaltProcessor;
import org.softwareFm.crowdsource.server.doers.internal.SignUpChecker;
import org.softwareFm.crowdsource.server.doers.internal.TakeOnProcessor;

public class ServerDoers implements IServerDoers {
	private final ServerConfig serverConfig;
	private final SignUpChecker signUpChecker;
	private final SaltProcessor saltProcessor;
	private final IMailer mailer;
	private final LoginChecker loginChecker;
	private final TakeOnProcessor takeOnProcessor;
	private final PasswordResetter passwordResetter;
	private final ForgottonPasswordMailer forgottonPasswordMailer;
	private final EmailSaltRequester emailSaltRequester;

	public ServerDoers(ServerConfig serverConfig, IUserAndGroupsContainer container) {
		this.serverConfig = serverConfig;
		this.mailer = serverConfig.mailer;
		
		this.signUpChecker = new SignUpChecker(serverConfig.dataSource, serverConfig.cryptoGenerators, container);
		this.saltProcessor = new SaltProcessor();
		this.loginChecker = new LoginChecker(serverConfig.dataSource);
		this.takeOnProcessor = new TakeOnProcessor(container, serverConfig);
		this.passwordResetter = new PasswordResetter(serverConfig.dataSource);
		this.forgottonPasswordMailer = new ForgottonPasswordMailer(getMailer(), serverConfig.userCryptoAccess);
		this.emailSaltRequester = new EmailSaltRequester(serverConfig.dataSource);
	}

	@Override
	public IUsage getUsage() {
		return serverConfig.usage;
	}

	@Override
	public ISignUpChecker getSignUpChecker() {
		return signUpChecker;
	}

	@Override
	public ISaltProcessor getSaltProcessor() {
		return saltProcessor;
	}

	@Override
	public IMailer getMailer() {
		return mailer;
	}

	@Override
	public ILoginChecker getLoginChecker() {
		return loginChecker;
	}

	@Override
	public ITakeOnProcessor getTakeOnProcessor() {
		return takeOnProcessor;
	}

	@Override
	public IPasswordResetter getPasswordResetter() {
		return passwordResetter;
	}

	@Override
	public IForgottonPasswordMailer getForgottonPasswordMailer() {
		return forgottonPasswordMailer;
	}

	@Override
	public IEmailSaltRequester getEmailSaltRequester() {
		return emailSaltRequester;
	}
}
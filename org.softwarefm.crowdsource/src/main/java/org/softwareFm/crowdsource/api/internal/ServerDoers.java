/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
import org.softwareFm.crowdsource.navigation.IRepoNavigation;
import org.softwareFm.crowdsource.navigation.ServerRepoNavigation;
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
	private final IRepoNavigation repoNavigation;

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
		this.repoNavigation = new ServerRepoNavigation(serverConfig.root);
	}

	@Override
	public IRepoNavigation getRepoNavigation() {
		return repoNavigation;
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
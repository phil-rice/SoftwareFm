/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.server;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.IExtraCallProcessorFactory;
import org.softwareFm.crowdsource.api.IIdAndSaltGenerator;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.server.callProcessor.internal.EmailSailRequesterMock;
import org.softwareFm.crowdsource.server.callProcessor.internal.ForgottonPasswordProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.ForgottonPasswordProcessorMock;
import org.softwareFm.crowdsource.server.callProcessor.internal.ForgottonPasswordWebPageProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.LoginCheckerMock;
import org.softwareFm.crowdsource.server.callProcessor.internal.LoginProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.MakeSaltForLoginProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.PasswordResetterMock;
import org.softwareFm.crowdsource.server.callProcessor.internal.RequestEmailSaltProcessor;
import org.softwareFm.crowdsource.server.callProcessor.internal.SaltProcessorMock;
import org.softwareFm.crowdsource.server.callProcessor.internal.SignUpCheckerMock;
import org.softwareFm.crowdsource.server.callProcessor.internal.SignupProcessor;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;

abstract public class AbstractProcessorMockIntegrationTests extends AbstractProcessorIntegrationTests {
	protected SaltProcessorMock saltProcessor;
	protected LoginCheckerMock loginChecker;
	protected SignUpCheckerMock signUpChecker;
	protected ForgottonPasswordProcessorMock forgottonPasswordProcessor;
	protected PasswordResetterMock resetter;
	private EmailSailRequesterMock emailSaltProcessor;

	protected String signUpCrypto;
	private IIdAndSaltGenerator idAndSaltGenerator;

	@Override
	protected IExtraCallProcessorFactory getExtraProcessCalls() {
		saltProcessor = new SaltProcessorMock();
		loginChecker = new LoginCheckerMock("loginCrypto", "loginCheckersSoftwareFmId");
		signUpCrypto = Crypto.makeKey();
		signUpChecker = new SignUpCheckerMock(null, signUpCrypto);
		forgottonPasswordProcessor = new ForgottonPasswordProcessorMock(null);
		resetter = new PasswordResetterMock("theNewPassword");
		emailSaltProcessor = new EmailSailRequesterMock("someEmailHash");
		idAndSaltGenerator = IIdAndSaltGenerator.Utils.mockGenerators("someSoftwareFmId{0}", null, null, null);
		return new IExtraCallProcessorFactory() {
			@Override
			public ICallProcessor[] makeExtraCalls(IContainer api, ServerConfig serverConfig) {
				return new ICallProcessor[] { new LoginProcessor(saltProcessor, loginChecker), //
						new SignupProcessor(signUpChecker, saltProcessor, idAndSaltGenerator), //
						new MakeSaltForLoginProcessor(saltProcessor),//
						new RequestEmailSaltProcessor(emailSaltProcessor),//
						new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordProcessor),//
						new ForgottonPasswordWebPageProcessor(resetter) };
			}
		};
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getServerApi().getServer();
	}

}
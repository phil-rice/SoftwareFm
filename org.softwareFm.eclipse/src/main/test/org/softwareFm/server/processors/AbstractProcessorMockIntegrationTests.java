/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors;

import java.util.concurrent.Callable;

import org.easymock.EasyMock;
import org.softwareFm.common.IUser;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.processors.internal.EmailSailRequesterMock;
import org.softwareFm.server.processors.internal.ForgottonPasswordProcessor;
import org.softwareFm.server.processors.internal.ForgottonPasswordProcessorMock;
import org.softwareFm.server.processors.internal.ForgottonPasswordWebPageProcessor;
import org.softwareFm.server.processors.internal.LoginCheckerMock;
import org.softwareFm.server.processors.internal.LoginProcessor;
import org.softwareFm.server.processors.internal.MakeSaltForLoginProcessor;
import org.softwareFm.server.processors.internal.PasswordResetterMock;
import org.softwareFm.server.processors.internal.RequestEmailSaltProcessor;
import org.softwareFm.server.processors.internal.SaltProcessorMock;
import org.softwareFm.server.processors.internal.SignUpCheckerMock;
import org.softwareFm.server.processors.internal.SignupProcessor;

abstract public class AbstractProcessorMockIntegrationTests extends AbstractProcessorIntegrationTests {
	private ICrowdSourcedServer server;
	protected SaltProcessorMock saltProcessor;
	protected LoginCheckerMock loginChecker;
	protected SignUpCheckerMock signUpChecker;
	protected ForgottonPasswordProcessorMock forgottonPasswordProcessor;
	protected PasswordResetterMock resetter;
	private EmailSailRequesterMock emailSaltProcessor;
	private Callable<String> softwareFmIdGenerator;
	protected IUser userMock;

	protected String signUpCrypto;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		saltProcessor = new SaltProcessorMock();
		loginChecker = new LoginCheckerMock("loginCrypto", "loginCheckersSoftwareFmId");
		signUpCrypto = Crypto.makeKey();
		signUpChecker = new SignUpCheckerMock(null, signUpCrypto);
		forgottonPasswordProcessor = new ForgottonPasswordProcessorMock(null);
		resetter = new PasswordResetterMock("theNewPassword");
		emailSaltProcessor = new EmailSailRequesterMock("someEmailHash");
		softwareFmIdGenerator = Callables.patternWithCount("someSoftwareFmId{0}");
		userMock = EasyMock.createMock(IUser.class);
		server = ICrowdSourcedServer.Utils.testServerPort(IProcessCall.Utils.chain(//
				new LoginProcessor(saltProcessor, loginChecker), //
				new SignupProcessor(signUpChecker, saltProcessor, softwareFmIdGenerator), //
				new MakeSaltForLoginProcessor(saltProcessor),//
				new RequestEmailSaltProcessor(emailSaltProcessor),//
				new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordProcessor),//
				new ForgottonPasswordWebPageProcessor(resetter)), ICallback.Utils.rethrow());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
	}

}
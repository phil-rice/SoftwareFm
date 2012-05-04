/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import org.easymock.EasyMock;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.server.RequestLineMock;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class ChangePasswordProcessorTest extends AbstractProcessCallTest<ChangePasswordProcessor> {

	private final static String url = "/" + LoginConstants.changePasswordPrefix;
	private IUserCryptoAccess userCryptoAccess;

	public void testIgnoresGetsAndNoneCommands() {
		checkIgnoresNonePosts();
		checkIgnores(CommonConstants.GET, url);
	}

	public void testResetsPasswordWhenOk() {
		checkCalls("email", "hash", "newHash", true);
	}

	public void testWhenPasswordDoesntChange() {
		checkCalls("email", "hash", "newHash", false);
	}

	private void checkCalls(String email, String oldHash, String newHash, boolean expected) {
		EasyMock.expect(userCryptoAccess.changePassword(email, oldHash, newHash)).andReturn(expected);
		EasyMock.replay(userCryptoAccess);
		IProcessResult result = processor.process(new RequestLineMock(CommonConstants.POST, url), Maps.stringObjectMap(LoginConstants.emailKey, email, LoginConstants.passwordHashKey, oldHash, LoginConstants.newPasswordHashKey, newHash));
		if (expected)
			IProcessResult.Utils.	checkStringResult(result, LoginMessages.passwordChanged);
		else
			IProcessResult.Utils.	checkErrorResult(result, CommonConstants.notFoundStatusCode, LoginMessages.wrongPassword, LoginMessages.wrongPassword);
		EasyMock.verify(userCryptoAccess);
	}

	@Override
	protected void setUp() throws Exception {
		userCryptoAccess = EasyMock.createMock(IUserCryptoAccess.class);
		super.setUp();
	}

	@Override
	protected ChangePasswordProcessor makeProcessor() {
		return new ChangePasswordProcessor(userCryptoAccess);
	}

}
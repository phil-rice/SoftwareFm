/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.server.processors.IPasswordChanger;
import org.softwareFm.server.processors.IProcessResult;

public class ChangePasswordProcessorTest extends AbstractProcessCallTest<ChangePasswordProcessor> {

	private final static String url = "/" + LoginConstants.changePasswordPrefix;

	private PasswordChangerMock passwordChanger;

	public void testIgnoresGetsAndNoneCommands() {
		checkIgnoresNonePosts();
		checkIgnores(CommonConstants.GET, url);
	}

	public void testResetsPassword() {
		checkCalls("email", "hash", "newHash", true);
		checkCalls("email", "hash", "newHash", false);
	}

	private void checkCalls(String email, String oldHash, String newHash, boolean expected) {
		passwordChanger.setOk(expected);
		IProcessResult result = processor.process(new RequestLineMock(CommonConstants.POST, url), Maps.stringObjectMap(LoginConstants.emailKey, email, LoginConstants.passwordHashKey, oldHash, LoginConstants.newPasswordHashKey, newHash));
		if (expected)
			checkStringResult(result, LoginMessages.passwordChanged);
		else
			checkErrorResult(result, CommonConstants.notFoundStatusCode, LoginMessages.wrongPassword, LoginMessages.wrongPassword);
	}

	@Override
	protected ChangePasswordProcessor makeProcessor() {
		passwordChanger = IPasswordChanger.Utils.mockPasswordChanger();
		return new ChangePasswordProcessor(passwordChanger);
	}

}
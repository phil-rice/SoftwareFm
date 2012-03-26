/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.text.MessageFormat;

import org.softwareFm.crowdsource.api.server.AbstractProcessorMockIntegrationTests;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;

public class ResetPasswordIntegrationTest extends AbstractProcessorMockIntegrationTests {

	public void testWithNoError() throws Exception {
		String magicString = "theMagicString";
		resetPassword(magicString, IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, MessageFormat.format(LoginMessages.passwordResetHtml, "theNewPassword")));

	}

}
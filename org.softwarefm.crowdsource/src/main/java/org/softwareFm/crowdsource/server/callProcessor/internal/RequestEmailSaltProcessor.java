/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.Map;

import org.softwareFm.crowdsource.api.server.AbstractCallProcessor;
import org.softwareFm.crowdsource.api.server.IEmailSaltRequester;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class RequestEmailSaltProcessor extends AbstractCallProcessor {

	private final IEmailSaltRequester saltRequester;

	public RequestEmailSaltProcessor(IEmailSaltRequester saltRequester) {
		super(CommonConstants.POST, CommonConstants.emailSaltPrefix);
		this.saltRequester = saltRequester;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String email = Strings.nullSafeToString(parameters.get(LoginConstants.emailKey));
		String emailSalt = saltRequester.getSalt(email);
		if (emailSalt == null)
			return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, LoginMessages.emailNotRecognised);
		else
			return IProcessResult.Utils.processString(emailSalt);
	}

}
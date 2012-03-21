/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.crowdsource.api.server.AbstractCallProcessor;
import org.softwareFm.crowdsource.api.server.IPasswordResetter;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class ForgottonPasswordWebPageProcessor extends AbstractCallProcessor {

	private final IPasswordResetter resetter;

	public ForgottonPasswordWebPageProcessor(IPasswordResetter resetter) {
		super(CommonConstants.GET, LoginConstants.passwordResetLinkPrefix);
		this.resetter = resetter;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String magicString = Strings.lastSegment(actualUrl, "/");
		try {
			String newPassword = resetter.reset(magicString);
			if (newPassword == null)
				return IProcessResult.Utils.processString(MessageFormat.format(LoginMessages.failedToResetPasswordHtml, newPassword));
			else
				return IProcessResult.Utils.processString(MessageFormat.format(LoginMessages.passwordResetHtml, newPassword));
		} catch (Exception e) {
			return IProcessResult.Utils.processString(LoginMessages.failedToResetPasswordHtml);
		}
	}

}
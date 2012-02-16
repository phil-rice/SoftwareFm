/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.server.processors.IPasswordResetter;
import org.softwareFm.server.processors.IProcessResult;

public class ForgottonPasswordWebPageProcessor extends AbstractCommandProcessor {

	private final IPasswordResetter resetter;

	public ForgottonPasswordWebPageProcessor(IPasswordResetter resetter) {
		super(null, CommonConstants.GET, LoginConstants.passwordResetLinkPrefix);
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
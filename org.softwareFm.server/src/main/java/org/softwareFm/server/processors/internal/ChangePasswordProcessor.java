/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.server.processors.IPasswordChanger;
import org.softwareFm.server.processors.IProcessResult;

public class ChangePasswordProcessor extends AbstractCommandProcessor {

	private final IPasswordChanger passwordChanger;

	public ChangePasswordProcessor(IPasswordChanger passwordChanger) {
		super(null, CommonConstants.POST, LoginConstants.changePasswordPrefix);
		this.passwordChanger = passwordChanger;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String email = (String) parameters.get(LoginConstants.emailKey);
		String oldHash = (String) parameters.get(LoginConstants.passwordHashKey);
		String newHash = (String) parameters.get(LoginConstants.newPasswordHashKey);
		if (passwordChanger.changePassword(email, oldHash, newHash))
			return IProcessResult.Utils.processString(LoginMessages.passwordChanged);
		else
			return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, LoginMessages.wrongPassword);

	}
}
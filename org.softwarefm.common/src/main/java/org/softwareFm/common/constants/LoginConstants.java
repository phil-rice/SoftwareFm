/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.constants;

import org.softwareFm.common.url.IUrlGenerator;

public class LoginConstants {

	public static final String emailKey = "email";
	public static final String monikerKey = "moniker";
	public static final String passwordKey = "password";
	public static final String confirmPasswordKey = "confirmPassword";

	public static final String passwordHashKey = "passwordHash";
	public static final String newPasswordHashKey = "newPasswordHash";
	public static final String sessionSaltKey = "sessionSalt";
	public static final String emailSaltKey = "emailSalt";
	public static final String softwareFmIdKey = "softwareFmId";
	public static final String cryptoKey = "crypto";

	public static final String makeSaltPrefix = "command/makeLoginSalt";
	public static final String loginCommandPrefix = "command/login";
	public static final String forgottonPasswordPrefix = "command/forgottonPassword";
	public static final String passwordResetLinkPrefix = "command/resetPassword";
	public static final String signupPrefix = "command/signup";
	public static final String changePasswordPrefix = "command/changePassword";

	public static IUrlGenerator userGenerator(String prefix) {
		return IUrlGenerator.Utils.generator(prefix + "/users/{0}/{1}/{2}", softwareFmIdKey);
	}
}
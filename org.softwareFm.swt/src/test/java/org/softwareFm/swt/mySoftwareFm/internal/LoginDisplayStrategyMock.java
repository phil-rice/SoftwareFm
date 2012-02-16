/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.mySoftwareFm.internal;

import java.util.List;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.swt.mySoftwareFm.ILoginDisplayStrategy;

public class LoginDisplayStrategyMock implements ILoginDisplayStrategy {

	public final List<String> showLogin = Lists.newList();
	public final List<String> showForgetPassword = Lists.newList();
	public final List<String> showSignup = Lists.newList();
	public final List<String> showChangePassword = Lists.newList();
	public final List<String> initialEmails = Lists.newList();

	@Override
	public void showLogin(String sessionSalt, String initialEmail) {
		showLogin.add(sessionSalt);
		initialEmails.add(initialEmail);
	}

	@Override
	public void showForgotPassword(String sessionSalt, String initialEmail) {
		showForgetPassword.add(sessionSalt);
		initialEmails.add(initialEmail);
	}

	@Override
	public void showSignup(String sessionSalt, String initialEmail) {
		showSignup.add(sessionSalt);
		initialEmails.add(initialEmail);
	}

	@Override
	public void showChangePassword(String email) {
		showChangePassword.add(email);
	}

}